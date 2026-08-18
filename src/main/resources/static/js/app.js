/**
 * BlogApp Pro - Interactive Client Script (Mobile-Enhanced)
 */

const App = {
  getToken() {
    return localStorage.getItem('jwt_token');
  },

  setToken(token) {
    localStorage.setItem('jwt_token', token);
    // Set cookie for server-side page navigation
    document.cookie = `jwt_token=${token}; path=/; max-age=604800; SameSite=Lax`;
  },

  clearToken() {
    localStorage.removeItem('jwt_token');
    document.cookie = 'jwt_token=; path=/; max-age=0';
  },

  getUser() {
    const userStr = localStorage.getItem('user_info');
    return userStr ? JSON.parse(userStr) : null;
  },

  setUser(user) {
    localStorage.setItem('user_info', JSON.stringify(user));
  },

  logout() {
    this.clearToken();
    localStorage.removeItem('user_info');
    window.location.href = '/login';
  },

  showToast(message, type = 'success') {
    let container = document.querySelector('.toast-container');
    if (!container) {
      container = document.createElement('div');
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      setTimeout(() => toast.remove(), 300);
    }, 3500);
  },

  async api(endpoint, options = {}) {
    const token = this.getToken();
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    if (options.body instanceof FormData) {
      delete headers['Content-Type']; // Let browser set boundary
    }

    const response = await fetch(endpoint, {
      ...options,
      headers
    });

    if (response.status === 401) {
      // If unauthorized on a write action, prompt to login
      if (options.method && options.method !== 'GET') {
        App.showToast('Please sign in to complete this action', 'error');
      }
    }

    return response;
  },

  // Like Toggle Action
  async toggleLike(articleId, btnElement) {
    const token = this.getToken();
    if (!token) {
      this.showToast('Please sign in to like this article', 'error');
      setTimeout(() => window.location.href = '/login', 1000);
      return;
    }

    try {
      const res = await this.api(`/api/v1/articles/${articleId}/like`, { method: 'POST' });
      if (res.ok) {
        const data = await res.json();
        const countSpan = btnElement.querySelector('.like-count');
        if (countSpan) countSpan.textContent = data.likesCount;
        
        if (data.liked) {
          btnElement.classList.add('active');
          this.showToast('Article liked!');
        } else {
          btnElement.classList.remove('active');
        }
      }
    } catch (err) {
      this.showToast('Failed to update like status', 'error');
    }
  },

  // Bookmark Toggle Action
  async toggleBookmark(articleId, btnElement) {
    const token = this.getToken();
    if (!token) {
      this.showToast('Please sign in to bookmark this article', 'error');
      setTimeout(() => window.location.href = '/login', 1000);
      return;
    }

    try {
      const res = await this.api(`/api/v1/articles/${articleId}/bookmark`, { method: 'POST' });
      if (res.ok) {
        const data = await res.json();
        if (data.bookmarked) {
          btnElement.classList.add('bookmarked');
          this.showToast('Added to reading list');
        } else {
          btnElement.classList.remove('bookmarked');
          this.showToast('Removed from reading list');
        }
      }
    } catch (err) {
      this.showToast('Failed to update bookmark', 'error');
    }
  },

  // Post Comment Action
  async postComment(articleId, parentId = null) {
    const token = this.getToken();
    if (!token) {
      this.showToast('Please sign in to post a comment', 'error');
      setTimeout(() => window.location.href = '/login', 1000);
      return;
    }

    const textareaId = parentId ? `reply-input-${parentId}` : 'comment-main-input';
    const textarea = document.getElementById(textareaId);
    if (!textarea || !textarea.value.trim()) {
      this.showToast('Comment text cannot be empty', 'error');
      return;
    }

    try {
      const res = await this.api('/api/v1/comments', {
        method: 'POST',
        body: JSON.stringify({
          articleId: articleId,
          content: textarea.value.trim(),
          parentCommentId: parentId
        })
      });

      if (res.ok) {
        this.showToast('Comment posted successfully!');
        textarea.value = '';
        setTimeout(() => window.location.reload(), 600);
      } else {
        const error = await res.json();
        this.showToast(error.message || 'Failed to post comment', 'error');
      }
    } catch (err) {
      this.showToast('Error sending comment', 'error');
    }
  },

  // Delete Article Action
  async deleteArticle(articleId) {
    if (!confirm('Are you sure you want to delete this article? This action cannot be undone.')) {
      return;
    }

    try {
      const res = await this.api(`/api/v1/articles/${articleId}`, { method: 'DELETE' });
      if (res.ok) {
        this.showToast('Article deleted');
        setTimeout(() => window.location.href = '/dashboard', 600);
      } else {
        this.showToast('Failed to delete article', 'error');
      }
    } catch (err) {
      this.showToast('Error deleting article', 'error');
    }
  },

  initReadingProgressBar() {
    const bar = document.querySelector('.reading-progress-bar');
    if (!bar) return;

    window.addEventListener('scroll', () => {
      const winScroll = document.documentElement.scrollTop || document.body.scrollTop;
      const height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
      const scrolled = height > 0 ? (winScroll / height) * 100 : 0;
      bar.style.width = Math.min(100, Math.max(0, scrolled)) + '%';
    });
  },

  initNavbarAuth() {
    const user = this.getUser();
    const token = this.getToken();
    const isLoggedIn = Boolean(user && token);

    // Desktop Nav Items
    const authNav = document.getElementById('auth-nav-items');
    if (authNav) {
      if (isLoggedIn) {
        authNav.innerHTML = `
          <a href="/editor" class="btn btn-primary btn-sm">
            <svg width="15" height="15" fill="currentColor" viewBox="0 0 16 16"><path d="M12.854.146a.5.5 0 0 0-.707 0L10.5 1.793 14.207 5.5l1.647-1.646a.5.5 0 0 0 0-.708l-3-3zm.646 6.061L9.793 2.5 3.293 9H3.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.207l6.5-6.5zm-7.468 7.468A.5.5 0 0 1 6 13.5V13h-.5a.5.5 0 0 1-.5-.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.5-.5V10h-.5a.499.499 0 0 1-.175-.032l-.179.178a.5.5 0 0 0-.11.168l-2 5a.5.5 0 0 0 .65.65l5-2a.5.5 0 0 0 .168-.11l.178-.178z"/></svg>
            Write Story
          </a>
          <a href="/dashboard" class="nav-link">Dashboard</a>
          <a href="/bookmarks" class="nav-link">Bookmarks</a>
          <button onclick="App.logout()" class="btn btn-outline btn-sm">Logout</button>
        `;
      } else {
        authNav.innerHTML = `
          <a href="/login" class="nav-link">Sign In</a>
          <a href="/register" class="btn btn-primary btn-sm">Get Started</a>
        `;
      }
    }

    // Mobile Drawer Auth Items
    const mobileAuthNav = document.getElementById('mobile-auth-nav-items');
    if (mobileAuthNav) {
      if (isLoggedIn) {
        const initial = user.name ? user.name.charAt(0).toUpperCase() : 'U';
        mobileAuthNav.innerHTML = `
          <div class="mobile-user-profile">
            <div class="author-avatar">${initial}</div>
            <div class="mobile-user-meta">
              <div class="mobile-user-name">${user.name || 'Author'}</div>
              <div class="mobile-user-email">${user.email || ''}</div>
            </div>
          </div>
          <a href="/editor" class="btn btn-primary btn-mobile-action">
            <svg width="15" height="15" fill="currentColor" viewBox="0 0 16 16"><path d="M12.854.146a.5.5 0 0 0-.707 0L10.5 1.793 14.207 5.5l1.647-1.646a.5.5 0 0 0 0-.708l-3-3zm.646 6.061L9.793 2.5 3.293 9H3.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.207l6.5-6.5zm-7.468 7.468A.5.5 0 0 1 6 13.5V13h-.5a.5.5 0 0 1-.5-.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.5-.5V10h-.5a.499.499 0 0 1-.175-.032l-.179.178a.5.5 0 0 0-.11.168l-2 5a.5.5 0 0 0 .65.65l5-2a.5.5 0 0 0 .168-.11l.178-.178z"/></svg>
            Write Story
          </a>
          <a href="/dashboard" class="mobile-nav-link">
            <span class="mobile-nav-icon">📊</span>
            <span>Dashboard</span>
          </a>
          <a href="/bookmarks" class="mobile-nav-link">
            <span class="mobile-nav-icon">🔖</span>
            <span>Bookmarks</span>
          </a>
          <button onclick="App.logout()" class="btn btn-outline btn-mobile-logout">
            <span>🚪 Logout</span>
          </button>
        `;
      } else {
        mobileAuthNav.innerHTML = `
          <a href="/login" class="mobile-nav-link">
            <span class="mobile-nav-icon">🔑</span>
            <span>Sign In</span>
          </a>
          <a href="/register" class="btn btn-primary btn-mobile-action">
            Get Started
          </a>
        `;
      }
    }
  },

  initMobileNav() {
    const menuToggle = document.getElementById('mobile-menu-toggle');
    const menuDrawer = document.getElementById('mobile-menu-drawer');
    const searchToggle = document.getElementById('mobile-search-toggle');
    const searchDrawer = document.getElementById('mobile-search-drawer');

    if (menuToggle && menuDrawer) {
      menuToggle.addEventListener('click', (e) => {
        e.stopPropagation();
        const isOpen = menuDrawer.classList.toggle('active');
        menuToggle.classList.toggle('active', isOpen);
        menuToggle.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
        
        if (isOpen && searchDrawer) {
          searchDrawer.classList.remove('active');
          if (searchToggle) searchToggle.classList.remove('active');
        }
      });
    }

    if (searchToggle && searchDrawer) {
      searchToggle.addEventListener('click', (e) => {
        e.stopPropagation();
        const isOpen = searchDrawer.classList.toggle('active');
        searchToggle.classList.toggle('active', isOpen);
        
        if (isOpen) {
          if (menuDrawer) {
            menuDrawer.classList.remove('active');
            if (menuToggle) {
              menuToggle.classList.remove('active');
              menuToggle.setAttribute('aria-expanded', 'false');
            }
          }
          const input = searchDrawer.querySelector('input');
          if (input) setTimeout(() => input.focus(), 150);
        }
      });
    }

    // Close drawers when clicking outside
    document.addEventListener('click', (e) => {
      const navbar = document.querySelector('.navbar');
      if (navbar && !navbar.contains(e.target)) {
        if (menuDrawer && menuDrawer.classList.contains('active')) {
          menuDrawer.classList.remove('active');
          if (menuToggle) {
            menuToggle.classList.remove('active');
            menuToggle.setAttribute('aria-expanded', 'false');
          }
        }
        if (searchDrawer && searchDrawer.classList.contains('active')) {
          searchDrawer.classList.remove('active');
          if (searchToggle) searchToggle.classList.remove('active');
        }
      }
    });

    // Close on Escape key
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        if (menuDrawer) {
          menuDrawer.classList.remove('active');
          if (menuToggle) {
            menuToggle.classList.remove('active');
            menuToggle.setAttribute('aria-expanded', 'false');
          }
        }
        if (searchDrawer) {
          searchDrawer.classList.remove('active');
          if (searchToggle) searchToggle.classList.remove('active');
        }
      }
    });
  }
};

document.addEventListener('DOMContentLoaded', () => {
  App.initNavbarAuth();
  App.initMobileNav();
  App.initReadingProgressBar();
});

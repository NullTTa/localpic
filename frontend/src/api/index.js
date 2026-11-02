import client from './client';

export const authApi = {
  me: () => client.get('/auth/me'),
  login: (email, password) => client.post('/auth/login', { email, password }),
  logout: () => client.post('/auth/logout'),
  signup: (form) => client.post('/auth/signup', form),
};

export const postApi = {
  feed: ({ tab, page }) => {
    if (tab === 'FOLLOWING') return client.get('/posts/following', { params: { page } });
    return client.get('/posts', { params: { page, type: tab === 'ALL' ? undefined : tab } });
  },
  byMember: (memberId, page = 0) => client.get(`/posts/member/${memberId}`, { params: { page } }),
  ongoingEvents: () => client.get('/posts/events/ongoing'),
  detail: (postId) => client.get(`/posts/${postId}`),
  create: (formData) => client.post('/posts', formData),
  update: (postId, formData) => client.put(`/posts/${postId}`, formData),
  remove: (postId) => client.delete(`/posts/${postId}`),
  toggleLike: (postId) => client.post(`/posts/${postId}/like`),
};

export const commentApi = {
  list: (postId) => client.get(`/posts/${postId}/comments`),
  write: (postId, content) => client.post(`/posts/${postId}/comments`, { content }),
  remove: (commentId) => client.delete(`/comments/${commentId}`),
};

export const memberApi = {
  profile: (memberId) => client.get(`/members/${memberId}`),
  updateMe: (formData) => client.put('/members/me', formData),
  toggleFollow: (memberId) => client.post(`/members/${memberId}/follow`),
  recommend: () => client.get('/members/recommend'),
};

export const etcApi = {
  trending: () => client.get('/hashtags/trending'),
  search: (q, page = 0) => client.get('/search', { params: { q, page } }),
  notifications: () => client.get('/notifications'),
  unreadCount: () => client.get('/notifications/unread-count'),
  readAll: () => client.patch('/notifications/read-all'),
  insight: () => client.get('/insights/me'),
};

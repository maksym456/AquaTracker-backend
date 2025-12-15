import { test, expect } from '@playwright/test';

// this should PASS
test('Basic GET request to public API - Verify Playwright works', async ({ request }) => {
  const response = await request.get('/posts/1');  
  expect(response.ok()).toBeTruthy();  // ok() means 2xx status
  expect(response.status()).toBe(200);
  const body = await response.json();
  expect(body).toHaveProperty('id', 1);
  expect(body).toHaveProperty('title');
  console.log('Basic test passed! Playwright setup is working.');
});


import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './tests/api', 
  fullyParallel: true,     
  forbidOnly: !!process.env.CI,  
  retries: process.env.CI ? 2 : 0,  
  workers: process.env.CI ? 1 : undefined,  
  reporter: process.env.CI ? 'dot' : 'list',  
  use: {
    baseURL: 'https://jsonplaceholder.typicode.com/',  // Temporary public test API 
    extraHTTPHeaders: {
      'Content-Type': 'application/json',
    },
    trace: 'on-first-retry',  
  },
  projects: [
    {
      name: 'api',  
    },
  ],
});
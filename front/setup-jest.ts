import 'jest-preset-angular/setup-jest';

/* Suppress CSS parsing errors from jsdom */
const originalError = console.error;
const originalWarn = console.warn;
console.error = (...args: any[]) => {
  const errorMessage = args.join(' ');
  if (errorMessage.includes('Could not parse CSS stylesheet')) {
    return;
  }
  originalError.apply(console, args);
};
console.warn = (...args: any[]) => {
  const warnMessage = args.join(' ');
  if (warnMessage.includes('Could not parse CSS stylesheet')) {
    return;
  }
  originalWarn.apply(console, args);
};

/* global mocks for jsdom */
const mock = () => {
  let storage: { [key: string]: string } = {};
  return {
    getItem: (key: string) => (key in storage ? storage[key] : null),
    setItem: (key: string, value: string) => (storage[key] = value || ''),
    removeItem: (key: string) => delete storage[key],
    clear: () => (storage = {}),
  };
};

Object.defineProperty(window, 'localStorage', { value: mock() });
Object.defineProperty(window, 'sessionStorage', { value: mock() });
Object.defineProperty(window, 'getComputedStyle', {
  value: () => ['-webkit-appearance'],
});

Object.defineProperty(document.body.style, 'transform', {
  value: () => {
    return {
      enumerable: true,
      configurable: true,
    };
  },
});

/* output shorter and more meaningful Zone error stack traces */
// Error.stackTraceLimit = 2;

/**
 * @type {Cypress.PluginConfig}
 */
 import registerCodeCoverageTasks from '@cypress/code-coverage/task';

export default (on, config) => {
  // `on` is used to hook into various events Cypress emits
  // `config` is the resolved Cypress config
  registerCodeCoverageTasks(on, config);
  return config;
};

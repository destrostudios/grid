module.exports = {
  apps: [
    {
      name: 'grid',
      script: '/usr/lib/jvm/java-17-openjdk-amd64/bin/java',
      args: '-jar grid.jar',
      exp_backoff_restart_delay: 100,
    },
  ],
};

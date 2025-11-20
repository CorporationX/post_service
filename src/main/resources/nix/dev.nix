# To learn more about how to use Nix to configure your environment
# see: https://firebase.google.com/docs/studio/customize-workspace
{ pkgs, ... }: {
  # Which nixpkgs channel to use.
  channel = "stable-24.05"; # or "unstable"

  # Use https://search.nixos.org/packages to find packages
  packages = [
    # pkgs.go
    # pkgs.python311
    # pkgs.python311Packages.pip
    # pkgs.nodejs_20
    # pkgs.nodePackages.nodemon
    
    pkgs.jdk17
    pkgs.gradle
    pkgs.docker
    pkgs.docker-compose
    pkgs.service-wrapper
    pkgs.git
    pkgs.gh
    pkgs.zsh
    pkgs.iputils
    pkgs.postgresql
  ];

  # Sets environment variables in the workspace
  env = {
    SPRING_PROFILES_ACTIVE = "dev";
    DB_URL = "jdbc:postgresql://java-bc-001.postgres.database.azure.com:5432/postgres?user=kashkademan_admin&password=wiSxop-6pavdu-kafvom&sslmode=require";
    DB_USERNAME = "user";
    DB_PASSWORD = "password";
    EP_PAYMENT_SERVICE = ""; 
    EP_USER_SERVICE = "https://8080-idx-javabc-kd-user-service-1746433965399.cluster-3gc7bglotjgwuxlqpiut7yyqt4.cloudworkstations.dev";
    EP_PROJECT_SERVICE = "https://8082-firebase-javabc-kd-project-service-1747252604231.cluster-axf5tvtfjjfekvhwxwkkkzsk2y.cloudworkstations.dev";
    EP_POST_SERVICE = "https://8081-firebase-javabc-kd-post-service-1747807870140.cluster-l6vkdperq5ebaqo3qy4ksvoqom.cloudworkstations.dev";
  };
  services.docker.enable = true;
  idx = {
    # Search for the extensions you want on https://open-vsx.org/ and use "publisher.id"
    extensions = [
      # "vscodevim.vim"
      "vscodevim.vim"
      "ms-azuretools.vscode-docker"
      "mtxr.sqltools"
      "mtxr.sqltools-driver-pg"
      "fwcd.kotlin"
      "redhat.java"
      "vscjava.vscode-gradle"
      "vscjava.vscode-java-debug"
      "vscjava.vscode-java-dependency"
      "vscjava.vscode-java-pack"
      "vscjava.vscode-java-test"
      "vscjava.vscode-maven"
      "rangav.vscode-thunder-client"
      "EchoAPI.echoapi-for-vscode"
      "cweijan.dbclient-jdbc"
      "cweijan.vscode-mysql-client2"
      
    ];

    # Enable previews
    previews = {
      enable = true;
      previews = {
        # web = {
        #   # Example: run "npm run dev" with PORT set to IDX's defined port for previews,
        #   # and show it in IDX's web preview panel
        #   command = ["npm" "run" "dev"];
        #   manager = "web";
        #   env = {
        #     # Environment variables to set for your server
        #     PORT = "$PORT";
        #   };
        # };
      };
    };

    # Workspace lifecycle hooks
    workspace = {
      # Runs when a workspace is first created
      onCreate = {
        # Example: install JS dependencies from NPM
        # npm-install = "npm install";
      };
      # Runs when the workspace is (re)started
      onStart = {
        # Example: start a background task to watch and re-build backend code
        # watch-backend = "npm run watch-backend";
      };
    };
  };
}

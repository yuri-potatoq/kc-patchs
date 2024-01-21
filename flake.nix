{
  description = "Keycloak Provider Shell";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, utils }:
    utils.lib.eachDefaultSystem (system:
      let
        pname = "kc-providers";
        version = "0.0.1";
        ktVersion = "1.9.22";
        pkgs = import nixpkgs {
          inherit system;
        };
        config.allowUnfree = true;
        jdk = pkgs.temurin-bin-21;
        toolchains = [jdk];

        build-tools = with pkgs; [
          # https://discourse.nixos.org/t/overriding-the-jdk-for-gradle-in-a-nix-flake/36541
          (callPackage gradle-packages.gradle_8 {
            java = jdk;
          })
          
          (kotlin.overrideAttrs (curr: old: { 
            version = ktVersion;
            src = fetchurl {
              url = "https://github.com/JetBrains/kotlin/releases/download/v${ktVersion}/kotlin-compiler-${ktVersion}.zip";
              sha256 = "sha256-iLOSE1BlMsgW/1Y0jAe77v4MjRiUO/+60RBjz5fKw+Y=";
            };
          }))
          # https://github.com/NixOS/nixpkgs/issues/207153
          # (gradle_8.overrideAttrs (curr: old: {
          #   buildInputs = [ jdk ];
          #   fixupPhase = old.fixupPhase + ''
          #     cat > $out/lib/gradle/gradle.properties <<EOF
          #     org.gradle.java.installations.paths=${pkgs.lib.concatStringsSep "," toolchains}
          #     EOF
          #   '';
          # }))
          podman-compose
          just
          kcat
        ];
      in
      rec {
        # `nix develop`
        devShell = with pkgs; mkShell {
          buildInputs = [ jdk ] ++ build-tools;
        };
      });
}

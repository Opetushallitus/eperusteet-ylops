#!/bin/bash
set -euo pipefail

# Generoi openapi-kuvaukset
gen_openapi() {
  cd eperusteet-ylops-service/ \
    && mvn clean verify -Pspringdoc \
    && cp target/openapi/ylops.spec.json ../generated
  cd ..
}

# Generoi julkiset openapi-kuvaukset
gen_openapi_ext() {
  cd eperusteet-ylops-service/ \
    && mvn clean verify -Pspringdoc-ext \
    && cp target/openapi/ylops-ext.spec.json ../generated
  cd ..
}

# Dispatch based on argument
case "${1:-}" in
  ext)
    gen_openapi_ext
    ;;
  "")
    gen_openapi
    ;;
  *)
    echo "Usage: $0 [ext]"
    echo "  (no args)  - generate standard OpenAPI spec"
    echo "  ext       - generate extended/public OpenAPI spec"
    exit 1
    ;;
esac

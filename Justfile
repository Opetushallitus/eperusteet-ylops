# Generoi openapi-kuvaukset
gen_openapi:
	@cd eperusteet-ylops-service/ \
		&& mvn clean verify -Pspringdoc \
		&& cp target/openapi/ylops.spec.json ../generated
		
# Generoi julkiset openapi-kuvaukset
gen_openapi_ext:
	@cd eperusteet-ylops-service/ \
		&& mvn clean verify -Pspringdoc-ext \
		&& cp target/openapi/ylops-ext.spec.json ../generated

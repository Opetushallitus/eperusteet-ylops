# Generoi openapi-kuvaukset
gen_openapi:
	@cd eperusteet-ylops-service/ \
		&& mvn clean compile -P generate-openapi \
		&& cp target/openapi/ylops.spec.json ../generated
		
# Generoi julkiset openapi-kuvaukset
gen_openapi_ext:
	@cd eperusteet-ylops-service/ \
		&& mvn clean compile -P generate-openapi-ext \
		&& cp target/openapi/ylops-ext.spec.json ../generated

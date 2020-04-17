# Generoi openapi-kuvaukset
gen_openapi:
	@cd eperusteet-ylops-service/ \
		&& mvn clean compile -P generate-openapi \
		&& cp target/openapi/ylops.spec.json ../generated

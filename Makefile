default:
	@docker run --rm -v $(PWD):/idear -w /idear openasr/idear ./gradlew buildPlugin --stacktrace

test:
	@docker run --rm -v $(PWD):/idear -w /idear openasr/idear ./gradlew runPluginVerifier test --stacktrace

docker:
	@rm -rf build out
	@docker build -t openasr/idear .

push:
	@docker push openasr/idear:latest
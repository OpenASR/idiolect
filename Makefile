default:
	@docker run --rm -v $(PWD):/idear -w /idear openasr/idear ./gradlew buildPlugin

docker:
	@docker build -t openasr/idear .

push:
	@docker push openasr/idear:latest


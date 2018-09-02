default:
	@docker run --rm -v $(PWD):/idear -w /idear openasr/idear ./gradlew buildPlugin

test:
	@docker run --rm -v $(PWD):/idear -w /idear idear ./gradlew test

docker:
	@rm -rf build out
	@docker build -t openasr/idear .

push:
	@docker push openasr/idear:latest


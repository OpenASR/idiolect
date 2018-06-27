default:
	@docker run --rm -v $(PWD):/idear -w /idear idear ./gradlew buildPlugin

test:
	@docker run --rm -v $(PWD):/idear -w /idear idear ./gradlew test

docker:
	@rm -rf build out
	@docker build -t idear .

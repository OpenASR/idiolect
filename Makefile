default:
	@docker run --rm -v $(PWD):/idear -w /idear idear ./gradlew buildPlugin

docker:
	@docker build -t idear .

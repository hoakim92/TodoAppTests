TODO APP TESTS
HOW TO RUN
0) run TODO app
   docker run -p 8080:4242 todo-app ./app -e VERBOSE=1
1) run tests
   mvn test
   
test config (src/test/resources/application.properties)
url - url for TODO app API
user - user for DELETE request
password - password for DELETE request
threadCount - thread count for performance test
requestsCount - requests count for performance tes

COMMENTS
testCRUDChain - always fails(most usual bug, should be discussed)
Short summary: we can update deleted todo
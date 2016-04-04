# RegistryService
RESTful Registry Service. Can serve as a registry of virtually anything ( birds, animals , universe, you name it! ). 

REGISTRY API:
Instructions below will explain how to build and run this application. Application is a RESTful API exposing a registry of resources. Right now, resource of type "bird" can be added, read and deleted from the registry. But the application can be extended to store and retrieve a registry of virtually "anything".

The application has 2 projects in it - commonregistry and bird. CommonRegistry project holds all the common pieces in the registry application ( that are common to say a bird and an animal or any other type that needed to be added to the registry). Bird project holds all the necessary pieces of code needed to add , read or retrieve a bird from the registry.

The API has been developed according to specs in https://gist.github.com/sebdah/265f4255cb302c80abd4. Some additional things done as part of this project -

1. Bird schema has two additional fields - version and updateDate. Both can be used in cases of updates. Since Mongodb is not transactional, version can be used to determine which version of the mongodb document that the client did read and wants to update now. For example, client did read bird document with id <1234> and  version 3. The client reads the json and does some updates to it and posts the updated json to the server. In case the version of the document <1234> in the db has already moved on to say version 6, depending upon the type of update the client is trying to do, the client should receive a 409 status code with appropriate information in the response.

2. updateDate can be updated to the current date in case of an update to a document.

3. ListAll birds API accepts three query parameters - skip, limit and visible . Skip and limit are used for pagination purposes. If visible query parameter is not used by the client, it defaults to true. The client has the option to set the query parameter to true or false and the birds with visibility true or false will be retrieved accordingly.

The application is built using Jersey (JAX-RS), Jackson, Morphia libraries and tested using tomcat7. Additionally flapdoodle libraries are used for unit testing. Instead of mocking db layer calls/ other pieces of code, flapdoodle provides a way to create "on-the-fly"  "in-memory" mongod instances which can be used for unit testing purposes and cleaned up once unit testing is done.

The application is developed on linux debian platform ( Ubuntu ). Build creates 2 artifacts - .war and .deb. The .deb can be used using dpkg -i command on a linux box to install the .war in to a servlet container ( say tomcat ) and also install the necessary config files.


BUILD INSTRUCTIONS : 

Required : Maven 2+

1. Build CommonRegistry project first and then build bird project - mvn clean install -DskipTests=true.[this will skip unit tests. For instructions on running unit tests, see below]. CommonRegistry is built as a jar and bird project uses the common registry jar. The bird project by itself is built as a war file which can be deployed into example : tomcat7 container.

2. All the dependencies needed for the project are in the pom.xml placed in the specified projects.

3. When you build the bird project it creates a debian which can be used to install the .war on the linux machine where you want to run this application. If the build is run on mac/windows, it may fail to create the debian file in which case the jdeb plugin needs to be removed from the pom.xml.

DEPLOY INSTRUCTIONS:

1.The Application needes 2 config files. Both of them are available under the root directory of the bird project. regConfig.xml contains the configurations ( like mongo db details ) that the app needs. log4j.xml contains the log4j configuration required for logging. 

2. Any changes to mongo host, port, username, password, db or mongo connection options should go into regConfig.xml

3. Any changes to the log4j configuration like changing the directory and name of file (default : /opt/logs/sl2.log ) should go into log4j.xml.

4. The file paths of both the above config files are passed into web.xml(bird/src/main/webapp/WEB-INF/web.xml) as init parameters. If you change the default paths of these files from the jdeb plugin then those corresponding paths needs to be updated in the web.xml also.

3. regConfig.xml and log4j.xml. If the application is built in a linux platform the debian created (in bird/target) can be installed using dpkg -i command, in which case the war file will be moved to /var/lib/tomcat7/webapps( assuming the war file is going to be deployed into tomcat7 ) , regConfig.xml will be moved to /etc/registry and log4j.xml will be moved to /etc/registry. Any changes to these paths should be done in the pom.xml of the bird project in the jdeb section. jdeb configuration in pom.xml also takes care of permissions and ownership of these directories in the linux machine. 

4. Any changes to these config file paths going into jdeb section in pom.xml or src/deb/control/control file should be done before building the project.

5. If the application is built and deployed in a non-deb platform , then the war file built should be copied into the required webapps directory manually. Also regConfig.xml and log4j.xml should also be copied manually into the desired directories and permissions and ownership should be set manually.

DEPLOY INSTRUCTIONS SUMMARY : 

1. Artifacts built : .war ,  .deb
2. config files - log4j.xml and regConfig.xml 
3. pom.xml has the jdeb configuration to move the config and war files to the right location when the deb is installed using dpkg -i command. 
4. log4j.xml and regConfig.xml default directory : /etc/registry
5. default log file /opt/logs/sl2.log
6. Config files are available under root directory of bird project.
7. If manually copying the config files, paths need to be updated in the src/main/webapp/WEB-INF/web.xml file and also src/deb/control/control file.

TEST INSTRUCTIONS ON LINUX:

1. Testing requires flapdoodle libraries which are downloaded as part of maven dependencies during build.
2. Additionally the mongodb binaries should be downloaded from the 10gen site and placed under the following directory  - 

   $HOME/.embedmongo/linux/mongodb-linux-x86_64-2.3.0.tgz.
3. If the downloaded binaries name is different, it needs to be renamed to match the above name format. Also the directories in the above structure should be created if not already.

4. when you run " mvn clean install ", flapdoodle libraries look for binaries in the above location which is used to create "on-the-fly"  "in-memory" mongod instances used to run tests.




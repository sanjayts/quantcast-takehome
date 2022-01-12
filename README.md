# quantcast-takehome
Repository for the Quantcast take home assignment

## Executing The Program

To test out the command line tool, simply use the executable provided in our target folder as 
shown below with the date and sample file of your choice:

`./target/cookie-store -f test-data/load-test.txt -d 2018-12-12`

To run with verbose logging enabled please run the following:

    export CS_ROOT_LEVEL=debug
    ./target/cookie-store -f test-data/load-test.txt -d 2018-12-12

    OR

    export CS_ROOT_LEVEL=trace # for dumping internal state

## Building From Source

If you are interested in building the code from source, please run the below command:

`./mvnw install`

This will create the necessary artifacts like the final JAR and the executable wrapper in the target
directory. This will also execute our Jacoco code coverage tool along with Spotbugs.

## Generating Test Data

The folder `test-data` has a few sample log files. If interested, the test generator class `DataGen` can
be used to generate random test data for volume testing. Simply run the above class in the IDE of
your choice, and it will create a file `load-test.txt` in the `test-data` folder.

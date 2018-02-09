package demo

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Admin
import org.apache.hadoop.hbase.client.Connection
import com.google.cloud.bigtable.hbase.BigtableConfiguration
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.client.ResultScanner
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.client.Table
import org.apache.hadoop.hbase.util.Bytes

@Transactional
class AppEngineService {

    private static String PROJECT_ID
    private static String INSTANCE_ID

// The initial connection to Cloud Bigtable is an expensive operation -- We cache this Connection
// to speed things up.  For this sample, keeping them here is a good idea, for
// your application, you may wish to keep this somewhere else.
    private static Connection connection = null     // The authenticated connection
    GrailsApplication grailsApplication


    /**
     * Connect will establish the connection to Cloud Bigtable.
     **/
    public void connect() throws IOException {

        if (PROJECT_ID == null || INSTANCE_ID == null ) {
            initConnnection()
             log.error("environment variables BIGTABLE_PROJECT, and BIGTABLE_INSTANCE need to be defined.")
            return
        }

        //connection = BigtableConfiguration.connect(PROJECT_ID, INSTANCE_ID)
        Configuration conf = new Configuration(true)
        InputStream inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("bigtable-demo.properties")
        //conf.addResource(inputStream)
        //conf.set
        connection = BigtableConfiguration.connect(PROJECT_ID,INSTANCE_ID)
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                connect()
            } catch (IOException e) {
                log.error("connect ", e)
            }
        }
        if (connection == null) {

                log.error("BigtableHelper-No Connection")

        }
        return connection
    }

    def serviceMethod() {
        initConnnection()
    }
    BigtableConfiguration bigtableConfiguration

    // Refer to table metadata names by byte array in the HBase API
    private static final byte[] TABLE_NAME = Bytes.toBytes("Hello-Bigtable")
    private static final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("cf1")
    private static final byte[] COLUMN_NAME = Bytes.toBytes("greeting")

    // Write some friendly greetings to Cloud Bigtable
    private static final String[] GREETINGS =
            [ "Hello World!", "Hello Cloud Bigtable!", "Hello HBase!" ]




    public void initConnnection() {
        // This will be invoked as part of a warmup request, or the first user
        // request if no warmup request was invoked.


        if (PROJECT_ID == null) {
            PROJECT_ID = grailsApplication.config.get("googlecloud.projectid")
        }
        if (INSTANCE_ID == null) {
            INSTANCE_ID = grailsApplication.config.get("googlecloud.bigTableInstance")
        }


       /* if (PROJECT_ID != null && PROJECT_ID.startsWith("@")) {
            PROJECT_ID = null
        }
        if (INSTANCE_ID != null && INSTANCE_ID.startsWith("@")) {
            INSTANCE_ID = null
        }

        if (PROJECT_ID == null) {
            PROJECT_ID = grailsApplication.config.get("googlecloud.projectid")
        }
        if (INSTANCE_ID == null) {
            INSTANCE_ID = grailsApplication.config.get("googlecloud.bigTableInstance")
        }*/

        /*try {
            connect()
        } catch (IOException e) {
            log.error("BigtableHelper - connect ", e)
        }
        if (connection == null) {
            sc.log("BigtableHelper-No Connection")
        }*/
    }


    public static String create(Connection connection) {
        try {
            // The admin API lets us create, manage and delete tables
            Admin admin = connection.getAdmin()
            // [END connecting_to_bigtable]

            // [START creating_a_table]
            // Create a table with a single column family
            HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME))
            descriptor.addFamily(new HColumnDescriptor(COLUMN_FAMILY_NAME))

            admin.createTable(descriptor)
            // [END creating_a_table]
        } catch (IOException e) {
            return "Table exists."
        }
        return "Create table " + Bytes.toString(TABLE_NAME)
    }



    /**
     * Connects to Cloud Bigtable, runs some basic operations and prints the results.
     */
    public String doHelloWorld() {

        StringBuilder result = new StringBuilder()

        // [START connecting_to_bigtable]
        // Create the Bigtable connection, use try-with-resources to make sure it gets closed
        Connection connection = getConnection()
        result.append(create(connection))
        result.append("<br><br>")
        try  {
            Table table = connection.getTable(TableName.valueOf(TABLE_NAME))

            // Retrieve the table we just created so we can do some reads and writes

            // [START writing_rows]
            // Write some rows to the table
            result.append("Write some greetings to the table<br>")
            for (int i = 0; i < GREETINGS.length; i++) {
                // Each row has a unique row key.
                //
                // Note: This example uses sequential numeric IDs for simplicity, but
                // this can result in poor performance in a production application.
                // Since rows are stored in sorted order by key, sequential keys can
                // result in poor distribution of operations across nodes.
                //
                // For more information about how to design a Bigtable schema for the
                // best performance, see the documentation:
                //
                //     https://cloud.google.com/bigtable/docs/schema-design
                String rowKey = "greeting" + i

                // Put a single row into the table. We could also pass a list of Puts to write a batch.
                Put put = new Put(Bytes.toBytes(rowKey))
                put.addColumn(COLUMN_FAMILY_NAME, COLUMN_NAME, Bytes.toBytes(GREETINGS[i]))
                table.put(put)
            }
            // [END writing_rows]

            // [START getting_a_row]
            // Get the first greeting by row key
            String rowKey = "greeting0"
            Result getResult = table.get(new Get(Bytes.toBytes(rowKey)))
            String greeting = Bytes.toString(getResult.getValue(COLUMN_FAMILY_NAME, COLUMN_NAME))
            result.append("Get a single greeting by row key<br>")
            // [END getting_a_row]
            result.append("     ")
            result.append(rowKey)
            result.append("= ")
            result.append(greeting)
            result.append("<br>")

            // [START scanning_all_rows]
            // Now scan across all rows.
            Scan scan = new Scan()

            result.append("Scan for all greetings:")
            ResultScanner scanner = table.getScanner(scan)
            for (Result row : scanner) {
                byte[] valueBytes = row.getValue(COLUMN_FAMILY_NAME, COLUMN_NAME)
                result.append("    ")
                result.append(Bytes.toString(valueBytes))
                result.append("<br>")
            }
            // [END scanning_all_rows]

        } catch (IOException e) {
            result.append("Exception while running HelloWorld: " + e.getMessage() + "<br>")
            result.append(e.toString())
            return result.toString()
        }

        return result.toString()
    }

}

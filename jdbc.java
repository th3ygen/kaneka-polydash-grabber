import java.io.Console;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class GetEASnap {

    public static void main(String[] args) {

        // Arguments: dasName afServerName [trustedConnection useDialog userName password port logLevel catalogName elementName]
        // Examples:
        // Trusted connection:                          java GetEASnap myDas myAF yes 5461 2 NuGreen B-210
        // Get prompted by the login dialog:            java GetEASnap myDas myAF no yes 5461 2 NuGreen B-210
        // User name and Password (not recommended):    java GetEASnap myDas myAF no no myUserName myPassword 5461 2 NuGreen B-210
        // B-210 is an Element in the NuGreen sample database

        int argsLength = args.length;
        int portNumberArgPosition = 4;
        Console console = System.console();
        String dasName = "10.230.255.7";
        String dataSourceName = "10.230.255.7";
        String isTrustedConnection = "No";
        String useDialog = "No";
        String userName = "polyp";
        String password = "K@Neka2345";
        String portNumber = "5461";
        String logLevel = "0";
        String catalogName = "Kaneka";
        String elementName = "Poly";

        /*------------------------ Get supplied arguments ------------------------*/
        /*if (argsLength >= 1) {
            dasName = args[0];
        }
        if (argsLength >= 2) {
            dataSourceName = args[1];
        }
        if (argsLength >= 3) {
            isTrustedConnection = args[2];
        }
        if (isStringFalseOrNo(isTrustedConnection)) {
            if (argsLength >= 4) {
                useDialog = args[3];
                portNumberArgPosition = 5;
                if (isStringFalseOrNo(useDialog)) {
                    portNumberArgPosition = 7;
                    if (argsLength >= 5)
                        userName = args[4];
                    if (argsLength >= 6)
                        password = args[5];
                }
            }
        }
        if (argsLength >= portNumberArgPosition)
            portNumber = args[portNumberArgPosition - 1];
        if (argsLength >= portNumberArgPosition + 1)
            logLevel = args[portNumberArgPosition];
        if (argsLength >= portNumberArgPosition + 2)
            catalogName = args[portNumberArgPosition + 1];
        if (argsLength >= portNumberArgPosition + 3)
            elementName = args[portNumberArgPosition + 2];
        */
	/*------------------------ Get supplied arguments end ------------------------*/

        /*------------------------ Prompt for arguments ------------------------*/
       	/*
	while (dasName.isEmpty()) {
            if (console != null)
                dasName = console.readLine("Enter Data Access Server name (required): ");
            else {
                System.err.println("Data Access Server name is required");
                System.exit(0);
            }
        }
        while (dataSourceName.isEmpty()) {
            if (console != null)
                dataSourceName = console.readLine("Enter AF Server name (required): ");
            else {
                System.err.println("AF Server name is required");
                System.exit(0);
            }
        }
        if (isTrustedConnection.isEmpty()) {
            if (console != null)
                isTrustedConnection = console.readLine("Use trusted connection? [No]: ");
            if (isTrustedConnection.isEmpty())
                isTrustedConnection = "No";
        }

        boolean credentialsNeeded = isStringFalseOrNo(isTrustedConnection) && (useDialog.isEmpty()
                || (isStringFalseOrNo(useDialog) && (userName.isEmpty() || password.isEmpty())));

        if (credentialsNeeded) {
            if (console != null) {
                if (useDialog.isEmpty()) {
                    useDialog = console
                            .readLine("Do you have want to provide credentials via the log-in dialog? [No]:");
                    if (useDialog.isEmpty())
                        useDialog = "No";
                }
                if (isStringFalseOrNo(useDialog)) {
                    char[] passwordCharArray = {};
                    while (userName.isEmpty())
                        userName = console.readLine("Enter user name (required): ");
                    while (passwordCharArray.length == 0)
                        passwordCharArray = console.readPassword("Enter password (required): ");

                    password = new String(passwordCharArray);
                }
            } else {
                System.err.println("Credentials are required when trusted connection is not used.");
                System.exit(0);
            }
        }
        if (portNumber.isEmpty()) {
            if (console != null)
                portNumber = console.readLine("Enter the port number for the connection [5461]: ");
            if (portNumber.isEmpty())
                portNumber = "5461";
        }
        if (logLevel.isEmpty()) {
            if (console != null)
                logLevel = console.readLine("Enter PI JDBC log level [0]: ");
            if (logLevel.isEmpty())
                logLevel = "0";
        }
        if (catalogName.isEmpty()) {
            if (console != null)
                catalogName = console.readLine("Enter the catalog name [NuGreen]: ");
            if (catalogName.isEmpty())
                catalogName = "NuGreen";
        }
        if (elementName.isEmpty()) {
            if (console != null)
                elementName = console.readLine("Enter the AF Element name [B-210]: ");
            if (elementName.isEmpty())
                elementName = "B-210";
        }
        */
	/*------------------------ Prompt for arguments end ------------------------*/

        /*------------------------ Print arguments ------------------------*/
        String credentialsString = "\tUse trusted connection?: ";
        if (isStringFalseOrNo(isTrustedConnection)) {
            isTrustedConnection = "No";
            credentialsString += isTrustedConnection + "\n";

            if (isStringFalseOrNo(useDialog))
                useDialog = "No";
            else
                useDialog = "Yes";

            credentialsString += "\tUse the log-in dialog for credentials?: " + useDialog + "\n";

            if (useDialog.equals("No")) {
                credentialsString += "\tUser name: " + userName + "\n";
                credentialsString += "\tPassword: ********\n";
            }
        } else {
            isTrustedConnection = "Yes";
            credentialsString += isTrustedConnection + "\n";
        }
        
        System.out.println("\nArguments:");
        System.out.println("\tData Access Server Name: " + dasName);
        System.out.println("\tPI AF Server Name: " + dataSourceName);
        System.out.print(credentialsString);
        System.out.println("\tPort number: " + portNumber);
        System.out.println("\tLog level: " + logLevel);
        System.out.println("\tCatalog name: " + catalogName);
        System.out.println("\tAF Element name: " + elementName + "\n");
        /*------------------------ Print arguments end ------------------------*/

        /*------------------------ Get data using PI JDBC driver ------------------------*/
        Connection connection = null;
        String url = "";
        String driverClassName = "com.osisoft.jdbc.Driver";
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;
        Properties properties = new Properties();

        url = "jdbc:pioledbent://" + dasName + "/Data Source=" + dataSourceName + "; Integrated Security=SSPI";

        properties.put("TrustedConnection", isTrustedConnection);
        if (isTrustedConnection.equals("No") && useDialog.equals("No")) {
            properties.put("user", userName);
            properties.put("password", password);
        }
        properties.put("Port", portNumber);
        properties.put("LogConsole", "True");
        properties.put("LogLevel", logLevel);

        try {
            Class.forName(driverClassName).newInstance();
            connection = DriverManager.getConnection(url, properties);
            // Specify AF database name (catalog), so the query does not need to be catalog specific
            connection.setCatalog(catalogName);

            pStatement = connection.prepareStatement("SELECT ea.Name, s.ValueStr "
                            + "FROM [Asset].[ElementHierarchy] eh "
                            + "INNER JOIN [Asset].[ElementAttribute] ea ON ea.ElementID = eh.ElementID "
                            + "INNER JOIN [Data].[Snapshot] s ON s.ElementAttributeID = ea.ID "
                            + "WHERE eh.Name = ? "
                            + "ORDER BY ea.Name " + "OPTION (FORCE ORDER, EMBED ERRORS)");

            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println(metaData.getDriverName() + " " + metaData.getDriverVersion());
            System.out.println(metaData.getDatabaseProductName());
            System.out.println(metaData.getDatabaseProductVersion() + "\n");

            // Bind parameter representing the Element name
            pStatement.setString(1, elementName);
            resultSet = pStatement.executeQuery();

            // Read the data
            while (resultSet.next()) {
                String value, element;
                element = resultSet.getString(1);
                value = resultSet.getString(2);
                System.out.format("%20s  %s%n", element, value);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (pStatement != null)
                    pStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        /*------------------------ Get data using PI JDBC driver end------------------------*/
    }

    private static boolean isStringFalseOrNo(String argument) {
        return argument.toLowerCase().startsWith("f") || argument.toLowerCase().startsWith("n");
    }

}

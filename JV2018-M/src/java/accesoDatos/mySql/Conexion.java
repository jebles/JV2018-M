/** 
 * Proyecto: Juego de la vida.
 * Establece acceso a la base de datos OO db4o.
 * Aplica el patron Singleton.
 * @since: prototipo2.1
 * @source: Conexion.java 
 * @version: 2.2 - 2019/05/22
 * @author: ajp 
 */

package accesoDatos.mySql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import config.Configuracion;

public class Conexion {

	// Singleton
	private static Connection db;
	private String url;
	private String usr;
	private String passwd;

	private Conexion() {
		
		url = Configuracion.get().getProperty("mySql.url");
		usr = Configuracion.get().getProperty("mySql.user");
		passwd = Configuracion.get().getProperty("mySql.passwd");
		
		initConexion();
	}

	/**
	 * Configura la conexion.
	 * https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-usagenotes-connect-drivermanager.html
	 */
	private void initConexion() {
		try {
			// The newInstance() call is a work around for some
			// broken Java implementations
			//Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			
			// registrar/activar driver mysql
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			/**
			 * zona horaria del servidor para evitar error
			 */
			String zonaHoraria = "?useTimezone=true&serverTimezone=UTC";
			
		/**
		 * decimos donde está la conexión: 
		 * protocolo jdbc mysql = jdbc:mysql://
		 * /servidor/basededatos? 
		 * user=usuario&password=laquesea
		 */					
			
		    db = DriverManager.getConnection(url+zonaHoraria, usr, passwd);
		    //("jdbc:mysql://localhost/test?user=minty&password=greatsqldb");
		    // Do something with the Connection
		    
		} 
		catch ( ClassNotFoundException  | SQLException e  ) {
			e.printStackTrace();			   
		}
	}

	/**
	 *  Método estático de acceso a la instancia única.
	 *  Si no existe la crea invocando al constructor interno.
	 *  Utiliza inicialización diferida.
	 *  Sólo se crea una vez; instancia única -patrón singleton-
	 *  @return instance
	 */
	public static Connection getDB() {
		if (db == null) {
			new Conexion();
		}
		return db;
	}

	/**
	 * Cierra conexion.
	 */
	public static void cerrarConexiones() {
		if (db != null) {
			try {
				db.close();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

} // class

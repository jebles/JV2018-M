/** 
 * Proyecto: Juego de la vida.
 * Resuelve todos los aspectos del almacenamiento de objetos Usuario 
 * utilizando acceso a base de datos db4o.
 * Colabora en el patron Fachada.
 * @since: prototipo2.0
 * @source: UsuariosDAO.java 
 * @version: 2.1 - 2019/05/03 
 * @author: ajp
 */

package accesoDatos.mySql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import accesoDatos.DatosException;
import accesoDatos.OperacionesDAO;
import config.Configuracion;
import modelo.*;
import modelo.Usuario.RolUsuario;
import util.Fecha;
import util.Formato;


public class UsuariosDAO  implements OperacionesDAO {

	// Singleton. 
	private static UsuariosDAO instance = null;

	// Base datos mySQL
	private Connection db;
	private Statement stUsuarios;
	private ResultSet rsUsuarios;
	private DefaultTableModel tmUsuarios;
	private ArrayList<Usuario> bufferUsuarios;
	// private Statement sentenciaId;
	// private DefaultTableModel 

	/**
	 *  Método estático de acceso a la instancia única.
	 *  Si no existe la crea invocando al constructor interno.
	 *  Utiliza inicialización diferida.
	 *  Sólo se crea una vez; instancia única -patrón singleton-
	 *  @return instance
	 */
	public static UsuariosDAO getInstance() {
		if (instance == null) {
			instance = new UsuariosDAO();
		}
		return instance;
	}

	/**
	 * Constructor por defecto de uso interno.
	 * Sólo se ejecutará una vez.
	 */
	private UsuariosDAO() {
		//db= Conexion.getDB();
		try {
			inicializar();			
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		if (obtener("AAA0T") == null && obtener("III1R") == null) {
			cargarPredeterminados();
		}
	}



	private void inicializar() throws SQLException {
		db= Conexion.getDB();
		try {
			crearTablaUsuarios();
			this.stUsuarios = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			//sentenciaUsr.executeQuery("select * from usuarios");
		}
		catch (SQLException e) {
			crearTablaUsuarios();
		}

		//		try {
		//			sentenciaId = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
		//			sentenciaId.executeQuery("select * from usuarios where ");
		//			
		//		}
		//		catch (SQLException e) {
		//			crearTablaEquivalId();
		//		}
	}

	private void crearTablaEquivalId() {
		// TODO Auto-generated method stub

	}

	private void crearTablaUsuarios() throws SQLException {
		Statement s= db.createStatement();
		s.executeUpdate("create table usuarios ("
				+ "id varchar(5) not null primary key,"
				+ "nif varchar(9) not null,"
				+ "nombre varchar(45) not null,"
				+ "apellidos varchar(45) not null,"
				+ "calle varchar(45) not null,"
				+ "numero varhcar(5) not null,"
				+ "cp varchar(5) no null,"
				+ "poblacionvarchar(45) not null,"
				+ "correo varchar(45) not null,"
				+ "fechaNaciemiento date,"
				+ "fechaAlta date,"
				+ "claveAcceso varchar(16) not null,"				 
				//enum opcional pues ya lo verifica java, esperar a la conversion para ver si complesa tenerlo como doble seguridad
				//+ "rol enum('INVITADO','NORMAL','ADMINISTRADOR') default (INVITADO)"
				// alternativamente se puede dar por comprobado y ponerlo como un varchar
				+ "rol varchar(10) not null," 
				+ "unique (nif),"
				+ "unique (correo)"
				+ ");"
				);

	}

	@Override
	public List obtenerTodos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void alta(Object obj) throws DatosException {
		// TODO Auto-generated method stub

	}

	@Override
	public Usuario baja(String id) throws DatosException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void actualizar(Object obj) throws DatosException {
		// TODO Auto-generated method stub

	}

	@Override
	public String listarDatos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String listarId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void borrarTodo() {
		// TODO Auto-generated method stub

	}

	/**
	 *  Método para generar de datos predeterminados.
	 */
	private void cargarPredeterminados() {
		try {
			alta(new Usuario());	//Invitado.

			String nombre = Configuracion.get().getProperty("usuario.admin");
			alta(new Usuario(new Nif(Configuracion.get().getProperty("usuario.nifAdmin")), 
					nombre, 
					nombre + " " + nombre, 
					new DireccionPostal(), 
					new Correo(nombre.toLowerCase() + Configuracion.get().getProperty("correo.dominioPredeterminado")), 
					new Fecha(Configuracion.get().getProperty("usuario.fechaNacimientoPredeterminada")), 
					new Fecha(), 
					new ClaveAcceso(), 
					RolUsuario.ADMINISTRADOR)
					);
		} 
		catch (ModeloException | DatosException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Búsqueda de Usuario dado un objeto, reenvía al método que utiliza idUsr.
	 * @param obj - el Usuario a buscar.
	 * @return - el Usuario encontrado o null si no encuentra.
	 */
	public Usuario obtener(Object obj) {		
		assert obj != null;
		return this.obtener(((Usuario) obj).getId());
	}

	private void sincronizarBufferUsuarios() {
		bufferUsuarios.clear();
		for (int i = 0; i < tmUsuarios.getRowCount(); i++) {
			try {
				String id = (String) tmUsuarios.getValueAt(i,0);
				Nif nif = new Nif((String) tmUsuarios.getValueAt(i,1));
				Correo correo = new Correo((String) tmUsuarios.getValueAt(i,2));
				String nombre = (String) tmUsuarios.getValueAt(i,3);
				String apellidos = (String) tmUsuarios.getValueAt(i,4);
				DireccionPostal domicilio = new DireccionPostal ((String) tmUsuarios.getValueAt(i,5),
						(String) tmUsuarios.getValueAt(i,6),
						(String) tmUsuarios.getValueAt(i,7),
						(String) tmUsuarios.getValueAt(i,8));
				Fecha fechaNacimiento = new Fecha ((java.sql.Date) tmUsuarios.getValueAt(i,9));
				Fecha fechaAlta = new Fecha ((java.sql.Date) tmUsuarios.getValueAt(i,10));
				ClaveAcceso claveAcceso = new ClaveAcceso();
				claveAcceso.setTexto((String) tmUsuarios.getValueAt(i,11));
				RolUsuario rol = null;
				String rolString = ((String) tmUsuarios.getValueAt(i,12)) ;
				rol = obtenerRol(rolString);
				//genera y guarda objeto
				bufferUsuarios.add(new Usuario (nif,nombre,apellidos,domicilio, correo,
						fechaNacimiento, fechaAlta,claveAcceso, rol));
			}
			catch ( ModeloException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Obtiene el rol dado un string conel rol 
	 * @param i
	 * @return rol
	 */
	private RolUsuario obtenerRol(String rol) {
		//RolUsuario rolUsr = null;
		switch (rol) {		
		case "NORMAL":
			return  RolUsuario.NORMAL;
		case "ADMINISTRADOR":
			return  RolUsuario.ADMINISTRADOR;
		}
		return RolUsuario.INVITADO;
	}

	private void rellenarFilasModelo() {
		Object[] datosFila = new Object[this.tmUsuarios.getColumnCount()];
		// para cada fila en el ResultSet de la consulta
		try {
			while (rsUsuarios.next()) {
				//se replica y add fila en el tableModel
				for (int i = 0; i < datosFila.length; i++) {
					datosFila[i] = this.rsUsuarios.getObject(i+1);
				}
				this.tmUsuarios.addRow(datosFila);
			}		
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void borrarFilasModelo() {
		// TODO Auto-generated method stub
	}

	private void establecerColumnasModelo()  {

		try {
			//obtiene metadatos
			ResultSetMetaData metaDatos = this.rsUsuarios.getMetaData();
			// num total de col
			int numCol = metaDatos.getColumnCount();
			// etq de cada col
			Object[] etiquetas = new Object[numCol];
			for (int i = 0; i < numCol; i++) {
				etiquetas[i] = metaDatos.getColumnLabel(i+1);
			}
			//incorporar array de etq en el tableModel
			this.tmUsuarios.setColumnIdentifiers(etiquetas);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtiene un usuario dado su idUsr, el correo o su nif.
	 * @param id - el id de Usuario a buscar.
	 * @return - el Usuario encontrado. 
	 */
	@Override
	public Usuario obtener(String idUsr) {
		assert idUsr != null;

		ejecutarConsulta(idUsr);
		// set col and labels
		establecerColumnasModelo();
		// borrado previo de filas
		borrarFilasModelo();
		// volcado desde el resultSet
		rellenarFilasModelo();
		//actualiza buffer de objetos
		sincronizarBufferUsuarios();
		if (bufferUsuarios.size() > 0) {
			return (Usuario) bufferUsuarios.get(0);
		}
		return null;		
	}	

	private void ejecutarConsulta(String idUsr) {
		String id = "id";
		if (idUsr.matches(Formato.PATRON_NIF)) {
			id = "nif";
		}
		if (idUsr.matches(Formato.PATRON_CORREO1)) {
			id = "correo";
		}
		try {
			this.rsUsuarios = this.stUsuarios.executeQuery("select * from usuarios where "+ id +" = nif;");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

} //class
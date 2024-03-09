package existdbmaven.com.existdbmaven;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

public class App {
	public static void main(String[] args) throws Exception {
		String driver = "org.exist.xmldb.DatabaseImpl";
		Class cl = Class.forName(driver);

		Database database = (Database) cl.newInstance();

		// Propiedad para crear la base de datos si no existe
		database.setProperty("create-database", "true");

		// Registramos la base de datos
		DatabaseManager.registerDatabase(database);

		Collection col = DatabaseManager.getCollection("xmldb:exist://localhost:8083/exist/xmlrpc/db/ColeccionPruebas",
				"admin", "admin");
		
		//PARA LOCALIZAR SI EXISTE UN DOCUMENTO
		XMLResource res = null;
		res = (XMLResource) col.getResource("empleados.xml");
		if(res==null) {
			System.out.println("NO EXISTE EL DOCUMENTO");
		}
		
		XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		// Propiedad para que el resultado se muestre de forma legible
		service.setProperty("pretty", "true");
		// Propiedad para establecer la codificacion
		service.setProperty("encoding", "ISO-8859-1");

		// consulta a lanzar
		ResourceSet result = service.query("for $b in /EMPLEADOS/EMP_ROW[APELLIDO='TOVAR'] return $b");
		ResourceIterator i = result.getIterator();

		// Procesamos el resultado
		if (!i.hasMoreResources()) {
			System.out.println("LA CONSULTA NO DEVUELVE NADA");
		}
		while (i.hasMoreResources()) { // Procesamos el resultado
			Resource r = i.nextResource();
			System.out.println((String) r.getContent());
		}
		col.close();

	}
}

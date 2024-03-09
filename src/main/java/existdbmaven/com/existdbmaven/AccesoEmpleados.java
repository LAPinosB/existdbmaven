package existdbmaven.com.existdbmaven;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

public class AccesoEmpleados {

	private final String driver = "org.exist.xmldb.DatabaseImpl";
	Collection col = null;
	private XPathQueryService service = null; // Declarar XPathQueryService global

	public Collection conectar() {

		try {
			Class cl = Class.forName(driver); // Cargar del driver
			Database database = (Database) cl.newInstance(); // Instancia de la BD
			DatabaseManager.registerDatabase(database); // Registro del driver
			col = DatabaseManager.getCollection("xmldb:exist://localhost:8083/exist/xmlrpc/db/ColeccionPruebas",
					"admin", "admin");
			service = (XPathQueryService) col.getService("XPathQueryService", "1.0"); // Inicializar XPathQueryService
			service.setProperty("pretty", "true");
			service.setProperty("encoding", "ISO-8859-1");
			return col;
		} catch (XMLDBException e) {
			System.out.println("Error al inicializar la BD eXist.");
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Error en el driver.");
			// e.printStackTrace();
		} catch (InstantiationException e) {
			System.out.println("Error al instanciar la BD.");
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("Error al instanciar la BD.");
			// e.printStackTrace();
		}
		return null;
	}

	// Metodo para ver si un numero de empleado esta repetido.
	public boolean empleadoDuplicado(int empNo) {
		try {
			String query = String.format("count(/EMPLEADOS/EMP_ROW[EMP_NO='%d'])", empNo);
			ResourceSet result = service.query(query);
			if (result != null && result.getSize() > 0) {
				ResourceIterator i = result.getIterator();
				while (i.hasMoreResources()) {
					Resource r = i.nextResource();
					long count = Long.parseLong((String) r.getContent());
					return count > 1; // Si count > 1, significa que el número de empleado está duplicado
				}
			}
		} catch (Exception e) {
			e.printStackTrace();// me saldra en la consola el error y despues return false.
		}
		return false; // En caso de error o si no se encontraron resultados
	}

	// Método para listar todos los empleados
	public void listarEmpleados() {
		try {
			String query = "for $b in /EMPLEADOS/EMP_ROW return $b";
			ResourceSet result = service.query(query);
			printResults(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listarEmpleadosSinEtiquetasEjemploClase() {
		try {
	        String query = "for $b in /EMPLEADOS/EMP_ROW return string-join(($b/EMP_NO/text(), $b/APELLIDO/text(), $b/OFICIO/text(), $b/DIR/text(), $b/FECHA_ALT/text(), $b/SALARIO/text(), $b/DEPT_NO/text()), ', ')";
	        ResourceSet result = service.query(query);
	        if (result != null && result.getSize() > 0) {
	            ResourceIterator i = result.getIterator();
	            while (i.hasMoreResources()) {
	                Resource r = i.nextResource();
	                String empleado = (String) r.getContent();
	                System.out.println(empleado); // Imprimir todos los datos del empleado en una sola línea
	            }
	        } else {
	            System.out.println("No se encontraron empleados.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public void listarEmpleadosSinEtiquetas() {
		try {
			String query = "for $b in /EMPLEADOS/EMP_ROW return $b";
			ResourceSet result = service.query(query);
			if (result != null && result.getSize() > 0) {
				ResourceIterator i = result.getIterator();
				while (i.hasMoreResources()) {
					Resource r = i.nextResource();
					String empleado = (String) r.getContent();
					System.out.println(parseEmpleado(empleado));
				}
			} else {
				System.out.println("No se encontraron empleados.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String parseEmpleado(String empleado) {
		StringBuilder empleadoLinea = new StringBuilder();
		empleadoLinea.append("Numero empleado: ");
		empleadoLinea.append(obtenerValor(empleado, "EMP_NO"));
		empleadoLinea.append(", Nombre: ");
		empleadoLinea.append(obtenerValor(empleado, "APELLIDO"));
		empleadoLinea.append(", Oficio: ");
		empleadoLinea.append(obtenerValor(empleado, "OFICIO"));
		empleadoLinea.append(", DIR: ");
		empleadoLinea.append(obtenerValor(empleado, "DIR"));
		empleadoLinea.append(", FECHA_ALT: ");
		empleadoLinea.append(obtenerValor(empleado, "FECHA_ALT"));
		empleadoLinea.append(", SALARIO: ");
		empleadoLinea.append(obtenerValor(empleado, "SALARIO"));
		empleadoLinea.append(", DEPT_NO: ");
		empleadoLinea.append(obtenerValor(empleado, "DEPT_NO"));
		return empleadoLinea.toString();
	}

	private String obtenerValor(String empleado, String etiqueta) {
		int inicio = empleado.indexOf("<" + etiqueta + ">");
		if (inicio == -1) {
			return ""; // La etiqueta no se encontró, devolver una cadena vacía
		}
		inicio += etiqueta.length() + 2;
		int fin = empleado.indexOf("</" + etiqueta + ">");
		if (fin == -1) {
			return ""; // No se encontró la etiqueta de cierre, devolver una cadena vacía
		}
		return empleado.substring(inicio, fin);
	}

	/*
	 * private String obtenerValor(String empleado, String etiqueta) { int inicio =
	 * empleado.indexOf("<" + etiqueta + ">") + etiqueta.length() + 2; int fin =
	 * empleado.indexOf("</" + etiqueta + ">"); return empleado.substring(inicio,
	 * fin); }//--> Con este metodo Nos da el error de indexOutOfBoundesException
	 */

	public void consultarEmpleadoPorApellido(String apellido) {
		try {
			String query = String.format("for $b in /EMPLEADOS/EMP_ROW[APELLIDO='%s'] return $b", apellido);
			ResourceSet result = service.query(query);
			printResults(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void agregarEmpleado(String xmlEmpleado) {
		try {
			// Obtener el recurso XML existente
			Resource resource = col.getResource("empleados.xml");
			String xmlContent = (String) resource.getContent();

			// Insertar el nuevo empleado al final del contenido XML
			int index = xmlContent.lastIndexOf("</EMPLEADOS>");
			if (index != -1) {
				StringBuilder newXmlContent = new StringBuilder(xmlContent);
				newXmlContent.insert(index, xmlEmpleado);
				resource.setContent(newXmlContent.toString());
				col.storeResource(resource);
				System.out.println("Empleado agregado correctamente.");
			} else {
				System.out.println("No se encontró el elemento <EMPLEADOS> en el XML.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Método para actualizar los datos de un empleado por su número de empleado
	public void actualizarEmpleadoPorNumero(int empNo, String xmlEmpleadoActualizado) {
		try {
			String query = String.format("for $b in /EMPLEADOS/EMP_ROW[EMP_NO='%d'] return $b", empNo);
			ResourceSet result = service.query(query);
			if (result != null && result.getSize() > 0) {
				ResourceIterator i = result.getIterator();
				while (i.hasMoreResources()) {
					Resource r = i.nextResource();
					r.setContent(xmlEmpleadoActualizado);
				}
				System.out.println("Empleado actualizado correctamente.");
			} else {
				System.out.println("No se encontró ningún empleado con ese número de empleado.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void contarEmpleadosPorDepartamento(int deptNo) {
		try {
			String query = String.format("count(/EMPLEADOS/EMP_ROW[DEPT_NO='%d'])", deptNo);
			ResourceSet result = service.query(query);
			if (result != null && result.getSize() > 0) {
				ResourceIterator i = result.getIterator();
				while (i.hasMoreResources()) {
					Resource r = i.nextResource();
					System.out.println("Número de empleados en el departamento " + deptNo + ": " + r.getContent());
				}
			} else {
				System.out.println("No se encontró ningún empleado en el departamento " + deptNo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void consultarEmpleadoPorNumero(int empNo) {
		try {
			String query = String.format("/EMPLEADOS/EMP_ROW[EMP_NO='%d']", empNo);
			ResourceSet result = service.query(query);
			printResults(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void consultarEmpleadosPorRangoSalarial(int minSalario, int maxSalario) {
		try {
			String query = String.format("/EMPLEADOS/EMP_ROW[SALARIO >= %d and SALARIO <= %d]", minSalario, maxSalario);
			ResourceSet result = service.query(query);
			printResults(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void obtenerFechaAltaEmpleado(int empNo) {
		try {
			String query = String.format("/EMPLEADOS/EMP_ROW[EMP_NO='%d']/FECHA_ALT", empNo);
			ResourceSet result = service.query(query);
			printResults(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void obtenerEmpleadosConComision() {
		try {
			String query = "for $b in /EMPLEADOS/EMP_ROW[COMISION] return ($b/concat(APELLIDO/text(), ': ', COMISION/text()))";
			ResourceSet result = service.query(query);
			if (result != null && result.getSize() > 0) {
				ResourceIterator i = result.getIterator();
				while (i.hasMoreResources()) {
					Resource r = i.nextResource();
					System.out.println((String) r.getContent());
				}
			} else {
				System.out.println("No se encontró ningún empleado con comisión.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int obtenerUltimoNumeroEmpleado() {
		try {
			String query = "max(/EMPLEADOS/EMP_ROW/EMP_NO)";
			ResourceSet result = service.query(query);
			if (result != null && result.getSize() > 0) {
				ResourceIterator i = result.getIterator();
				if (i.hasMoreResources()) {
					Resource r = i.nextResource();
					String maxEmpleado = (String) r.getContent();
					return Integer.parseInt(maxEmpleado);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // Si ocurre un error o no se encuentra ningún empleado, devolvemos -1
	}

	// Método auxiliar para imprimir los resultados de una consulta Reutilizamos en
	// varios metodos asi de simple
	private void printResults(ResourceSet result) {
		try {
			if (result == null || result.getSize() == 0) {
				System.out.println("La consulta no devolvió resultados.");
				return;
			}

			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				System.out.println((String) r.getContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Método para obtener el servicio XPathQueryService global
	public XPathQueryService getXPathQueryService() {
		return service;
	}

}

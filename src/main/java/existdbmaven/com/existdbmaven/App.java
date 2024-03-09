package existdbmaven.com.existdbmaven;

public class App {
	public static void main(String[] args) throws Exception {
		AccesoEmpleados empleados = new AccesoEmpleados();
		empleados.conectar();
		int ultimoNumeroEmpleado = empleados.obtenerUltimoNumeroEmpleado();
		int nuevoNumeroEmpleado = ultimoNumeroEmpleado + 1;

        // XML del nuevo empleado a agregar con el nuevo número de empleado
        /*String nuevoEmpleado = "<EMP_ROW>\n" +
                "    <EMP_NO>" + nuevoNumeroEmpleado + "</EMP_NO>\n" +
                "    <APELLIDO>JIMENEZ</APELLIDO>\n" +
                "    <OFICIO>ABOGADO</OFICIO>\n" +
                "    <DIR>1234</DIR>\n" +
                "    <FECHA_ALT>2024-03-09</FECHA_ALT>\n" +
                "    <SALARIO>2000</SALARIO>\n" +
                "    <DEPT_NO>10</DEPT_NO>\n" +
                "</EMP_ROW>";*/

        // Agregar el nuevo empleado
        //empleados.agregarEmpleado(nuevoEmpleado);
        
        //empleados.consultarEmpleadoPorNumero(ultimoNumeroEmpleado);
		empleados.listarEmpleadosSinEtiquetasEjemploClase();
		//empleados.listarEmpleadosSinEtiquetas();
		//empleados.listarEmpleados();

	}
}

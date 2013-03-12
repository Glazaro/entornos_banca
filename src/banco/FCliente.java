
package banco;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @author Guillermo Lázaro.1º DAW
 * Clase que trata todo lo relacionado con los archivos de clientes
 */
public class FCliente {
    /**
     * variables de clase.
     * nregs para conocer el nº de registros que tiene el archivo
     * tam para conocer el tamaño de cada registro.
     */
   
    int nregs;
    int tam=144; //Para el nombre (80+2),para el DNI 18+2, para nº cuenta 40+2
    
    RandomAccessFile fes;//Declaración de objeto clase RandomAccessFile
   /**
         * Constructor para crear fichero Y fijar la variable del numero de registro de dicho fichero.
         */
    
    public FCliente() throws IOException {
        
        fes=new RandomAccessFile("clientes.txt","rw");
        nregs=(int)Math.ceil((double)fes.length()/(double)tam);
    }
    
    /**
         * Método que escribe los datos de cada cliente en archivo cliente.txt y crea el archivo individual de cada cuenta 
         */
    public void alta(){
        
        String dni,cuenta;
        int titulares=-1,salir=0;
        cuenta=calcularCuenta();//Llamo al método para que me devuelva un número de cuenta a usar
        do{//Do while para incluir varios titulares
            do{//do while para pedir un DNI correcto de 9 caracteres. No optimizado para la letra
                dni=Pantalla.pideCadena("Introduce el DNI del titular: " );
                if (dni.length()!=9){
                    Pantalla.muestra("El DNI tiene que tener 8 dígitos y una letra: ");
                }
            }while(dni.length()!=9);//fin 2 do while
            int pos=buscaDNI(dni);//Llamo a método buscaDNI
            CCliente cl;
                if (pos==-1){//Si busca DNI me devuelve -1 (no existe)
                   cl=new CCliente(dni,cuenta);//Creo objeto con un constructor que me devolverá el nombre
                }else{//Si existe me devuelve pos
                    String nombre=leer(pos).getNombre(); //Consigo el nombre con el método leer que me ha devuelto un objeto
                    cl=new CCliente(dni, nombre, cuenta);//Creo el objeto, con el constructor en el que ya le paso todos los datos.
                }
                if (escribirCliente (nregs,cl)){//Escribo cliente y si va todo bien, sumo 1 registro más (el que se ha creado).
                     nregs++;
                }
                if (titulares<4){//if para añadir hasta 4 titulares
                    salir=Pantalla.pideInt ("\n¿La cuenta tiene más titulares?.\n SI-1 \tNO-0 ");
                    if (salir!=0){
                        titulares++;
                    }
                }
      }while ((titulares<4) && (salir!=0)) ;//fin primer do while
      creaCuenta(cuenta);//Llamo a método crear cuenta, pasándole como parámetro el nº de cuenta que hemos creado.
      Pantalla.muestra("Enhorabuena, ya tenemos un trocito de su alma con el nº "+cuenta);
    }
        /**
         * @return pos o -1
         * Método para ver si esxiste ya el nº de DNI.
         * Si existe me devuelve la posición de ese objeto. si no me devuelve -1
         */
    public int buscaDNI(String dni){
        
        CCliente cl; //Sólo lo instancio porque leer me devolverá un nuevo objeto
        for(int i=0; i<nregs; i++){//con el for recorro todo el archivo.
            cl=leer(i);//Leo el objeto de clase Ccliente que hay en la posición i
            if (dni.compareToIgnoreCase(dni)==0){ //Uso método compare porque es cadena. Si lo encuentra compare devuelve 0
                return i; //Si encuentro DNI devuelvo posisicón, 
            }
        }
      return -1; //si no lo encuentra devuelve -1. 
    }
    /**
         * @return objeto de clase CCliente
         * Lee el objeto que se indica en la posición que recibe como parámetro.
         */
    public CCliente leer(int pos){
        
        String DNI,nombre,cuenta;
             try{//try porque puede devolver errores de lectura.
                 fes.seek(pos*tam);    //situamos el puntero en la posición que me indica i
                 DNI=fes.readUTF();
                 nombre=fes.readUTF();
                 cuenta=fes.readUTF();
                 return new CCliente (DNI,nombre,cuenta);
             }
             catch (IOException e){//tratamiento del error.
                 Pantalla.muestra("Problemas de lectura");
                 return null;    //si al intentar devolver objetoo, algo ha ido mal, se ejecuta esta linea.
             }
    
    }
    /**
         * @return boolean
         * Escribe el objeto recibido como parámetro en la posición que también le he enviado por parámetro
         * Devuelve true si todo va bien y false si va mal.
         */
    public boolean escribirCliente (int i,CCliente cliente){
        
        if(i>=0 && i<= nregs){
            try{//uso try porque puede generar errores
                fes.seek(i*tam);    //situamos el puntero en la posición que me indica i (será nregs*tamaño de cada registro)
                fes.writeUTF(cliente.getDNI());
                fes.writeUTF(cliente.getNombre());
                fes.writeUTF(cliente.getCuenta());
                return true;
            }
            catch (IOException e){//tratamiento del error
                Pantalla.muestra("Imposible escribir el registro");
            }
        }else{//No intentaría escribirlo, por eso no es necesario tratarlo como error.
            Pantalla.muestra("Registro excedido");
        }
        return false;
    }
    /**
         * @return cuenta
         * Calcula el nº de cuenta para que sea consecutivo
         */
    public String calcularCuenta(){
        
        String cuenta, siguiente;
        int siguienteint;
        
        CCliente cliente;//declaro objeto cliente (sin instanciar porque me lo devuelve luego el método leer.
        
        cliente=leer (nregs-1); //Leo el último registro.
        if(cliente==null){//Si cliente fuera null significa que es el primero e introduzco el nº de cuenta para ese primer cliente.
            cuenta="28015600000000000000";
        }else{
            cuenta=cliente.getCuenta();//consigo número de cuenta de último cliente.
            siguiente= cuenta.substring(7,20);//Extraigo los 12 últimos dígitos
            siguienteint=Integer.parseInt(siguiente);//Transformo la cadena a entero
            siguiente=String.format("%012d", siguienteint+1);//Paso el nuevo valor a cadena completando con el nº de 0 necesarios para que sean 12
            cuenta="28015600"+siguiente;//Construyo la cadena completamente
        }
        return cuenta;//devuelvo el nº de cuenta completo, en formato cadena.
        }
    /**
         * Nos enseña los datos de todos los clientes
         */
    public void listado(){
        
        CCliente cliente; // Sólo instancio el objeto porque recibiré uno luego.
        Pantalla.cabecera();
        Pantalla.cabeceraClientes();
        for(int i=0; i<nregs; i++){ //Con el for recorro todo el archivo
            cliente=leer(i);  //envío por parametro la posicion del registro y este me devuelve un objeto CCliente.
            cliente.muestraCliente(); //Enseño los datos del cliente usando al metodo muestraCliente() a traves del objeto de su clase.
        }
    }
    /**
         * Crea el archivo de cada cuenta corriente usando el constructor de la clase Fcuenta
         * Recibe como parámetro el nº de cuenta para pasárselo luego al constructor.
         */
    public void creaCuenta(String cuenta){
        
            FCuenta cc = null;
            try{//Uso try porque puede generar errores
                cc=new FCuenta(cuenta);  
            }
            catch(IOException e){//tratamiento del error.
                Pantalla.muestra("error en ficheros");
            }
    }
    /**
         * Método que permite seleccionar un nº de cuenta y abrir ese fichero para operar con él.
         * Para conocer el tamaño del array que necesito, uso variable tamarray (sacrifico velocidad a cambio de memoria. No óptimo si tengo muchos clientes.)
         */
    public void operaciones(){
        
        int tamarray=0, op;
        
        CCliente cliente;
        
        String dni=Pantalla.pideCadena("Introduzca nº DNI: ");
        for(int i=0; i<nregs; i++){//recorro el fichero entero con el for para saber cuantas cuentas tiene el cliente y pasar a crear el array con ese nº
            cliente=leer(i);
            if (dni.compareToIgnoreCase(cliente.getDNI())==0){//Si lo encuentra, suma uno
               tamarray++;
               System.out.println(cliente.getCuenta());
            }
        }
        System.out.println(tamarray);
        String[] cuentas= new String[tamarray];//creo array para guardar las cuentas del cliente.
        int j=0;//Variable para controlar la posición en el array
        for(int i=0; i<nregs; i++){//recorro el fichero entero con el for para guardar las cuentas del cliente.
            cliente=leer(i);
            if (dni.compareToIgnoreCase(cliente.getDNI())==0){ //si lo encuentra lo guarda en la posición del array correspondiente y le suma 1.  
                cuentas[j]=cliente.getCuenta();
                j++;
            }
        }
        
        for (int i=0;i<cuentas.length;i++){//recorro el array completo
             System.out.print("\n\t"+(i+1)+".- "+cuentas[i]);//Imprimo posición del array+1 (para empezar las opciones desde nº 1) y el valor del array      
        }    
        System.out.print("\n\t"+(cuentas.length+1)+".- Terminar");//imprimo la opción de salir.
        
        do {//do while para seleccionar la opción que desea el usario
           op=Pantalla.pideInt("\n\tSelecciona la cuenta: "); 
        } while (op<1 || op>(cuentas.length+1));
        if (op<=cuentas.length){
            
            FCuenta cta=null;
            try{//try porque puede generar error
                cta=new FCuenta(cuentas[op-1],1);//Pasa el nº de cuenta a operar y un int (irrelevante) por diseño de constructores.
            }
            catch (IOException e){//tratamiento del error.
                Pantalla.muestra("Problema con los archivos");
            }
        } 
    }
/**
         * Método para cerrar el archivo con su tratamiento de error.*/
    public void cerrarFichero(){
        
        try{
            fes.close();
        }
        catch(IOException e){
            Pantalla.muestra("Problemas con los ficheros");
        }
    }
}

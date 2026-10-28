package Exceptions;

public class EFueraRango extends Exception {
    public EFueraRango(int num1, int num2) {
        super("El numero debe estar entre" + num1 + " y " + num2);
    }
}

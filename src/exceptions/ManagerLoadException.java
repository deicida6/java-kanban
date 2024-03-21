package exceptions;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException() {
        System.out.println("Произошла ошибка загрузки из файла.");
    }
}
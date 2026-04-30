package pt.uab.musicaltrainer.api;

/**
 * Lançada quando um recurso pedido por ID não existe na BD.
 * Mapeada para HTTP 404 pelo GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

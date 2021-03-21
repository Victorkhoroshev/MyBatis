package net.thumbtack.school.elections.server.service;

//REVU: нужно перенести в другой пакет
public class ServerException extends Exception {

    private final ExceptionErrorCode voterExceptionErrorCode;

    public ServerException(ExceptionErrorCode voterExceptionErrorCode) {
        this.voterExceptionErrorCode = voterExceptionErrorCode;
    }

    @Override
    public String getLocalizedMessage() {
        return voterExceptionErrorCode.getMessage();
    }

    public ExceptionErrorCode getErrorCode() {
        return voterExceptionErrorCode;
    }

}
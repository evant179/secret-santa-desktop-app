package com.secretsanta.generator;

@SuppressWarnings("serial")
public class GenerateException extends Exception
{
    public GenerateException()
    {
        super();
    }

    public GenerateException(String message)
    {
        super(message);
    }

    public GenerateException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GenerateException(Throwable cause)
    {
        super(cause);
    }
}

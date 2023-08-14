package de.ars.rooms.reservations.lambda.jackson;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This request handler uses Jackson for JSON serialization.
 */
public abstract class AbstractJacksonRequestHandler<Input, Output> implements RequestStreamHandler {

    // handler initialization

    private static final ObjectMapper mapper = ObjectMapperHolder.MAPPER;

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        final var request = mapper.readValue(input, getInputClass());
        final var response = handleRequestWithObjects(request, context);
        mapper.writeValue(output, response);
    }

    protected abstract Class<? extends Input> getInputClass();

    protected abstract Output handleRequestWithObjects(Input input, Context context);

}

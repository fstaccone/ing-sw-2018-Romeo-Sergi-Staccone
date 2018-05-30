package it.polimi.ingsw.socket.responses;

import it.polimi.ingsw.socket.ResponseHandler;

public class CheckConnectionResponse implements Response {
    @Override
    public void handleResponse(ResponseHandler handler) {
        handler.handle(this);
    }
}

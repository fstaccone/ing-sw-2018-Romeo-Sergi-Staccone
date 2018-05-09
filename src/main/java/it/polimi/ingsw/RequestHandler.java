package it.polimi.ingsw;

public interface RequestHandler {
    Response handle(CheckUsernameRequest request);
    Response handle(CreateMatchRequest request);
    Response handle(AddPlayerRequest request);
}

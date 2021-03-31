package pt.tecnico.bicloin.hub.exceptions;

public class BadEntrySpecificationException extends Exception {

  private String _entrySpecification;

  public BadEntrySpecificationException(String entrySpecification) {
    _entrySpecification = entrySpecification;
  }

  public String getEntrySpecification() {
    return _entrySpecification;
  }
}
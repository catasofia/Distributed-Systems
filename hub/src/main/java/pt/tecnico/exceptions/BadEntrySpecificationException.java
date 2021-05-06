package pt.tecnico.bicloin.hub.exceptions;

import pt.tecnico.exceptions.ErrorMessage;

public class BadEntrySpecificationException extends Exception {

  private final ErrorMessage _entrySpecification;

  public BadEntrySpecificationException(ErrorMessage entrySpecification) {
    super(entrySpecification.label);
    _entrySpecification = entrySpecification;
  }

  public ErrorMessage getEntrySpecification() {
    return _entrySpecification;
  }
}
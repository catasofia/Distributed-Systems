package pt.tecnico.rec.exceptions;

public class BadEntrySpecificationException extends Exception {

  private ErrorMessage _entrySpecification;

  public BadEntrySpecificationException(ErrorMessage entrySpecification) {
    super(entrySpecification.label);
    _entrySpecification = entrySpecification;
  }

  public ErrorMessage getEntrySpecification() {
    return _entrySpecification;
  }
}
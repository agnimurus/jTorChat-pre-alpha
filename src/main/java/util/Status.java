package util;

public class Status {

  public static final byte OFFLINE = 0,
      HANDSHAKE = 1,
      ONLINE = 2,
      AWAY = 3,
      XA = 4; // Is XA referring to DTP specification (https://en.wikipedia.org/wiki/X/Open_XA), or Far Away (9th entry in en.ini language file)?

  //I would replace this class with an enum, but existing codebase relies on byte type. May refactor at later date
  //TODO determine if replacing class with enum is advisable
}

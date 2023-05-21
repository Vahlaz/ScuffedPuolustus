package components

import util.CompassDir

trait gameObject

class Path(val dirToNext: CompassDir) extends gameObject

class Rock extends gameObject

package org.mholford.chatlantis.lookup;

/**
 * Enumeration of possible attributes for a Permutable element
 * used in LUT generation.  Currently attributes are:<ul>
 *   <li>OPTIONAL - When permuting, will create two versions: one with the OPTIONAL element and
 *   one without</li>
 *   <li>NOT_FIRST - When permuting, this element cannot become the first element in the result</li>
 *   <li>NOT_LAST - When permuting, this element cannot become the last element in the result</li>
 * </ul>
 * Currently only OPTIONAL has been implemented.
 */
public enum GenAttr {
  OPTIONAL,
  NOT_FIRST,
  NOT_LAST
}

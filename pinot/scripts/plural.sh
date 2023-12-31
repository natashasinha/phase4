#!/bin/bash
# an imperfect pluralization algorithm that covers most use-cases and the data-types in this course.
# inspired from https://api.rubyonrails.org/classes/ActiveSupport/Inflector.html#method-i-inflections


#
# Why am I including this as part of this example? Well, because of me incorrectly
# saying 'aircrafts' insteadof 'aircraft' when doing a presentation.
#
function isAlsoPlural() {
  local singular_is_also_plural=("aircraft" "fish" "species" "sheep" "deer" "series" "offspring" "salmon" "shrimp" "trout" "headquarters" "means" "crossroads")
  for element in "${singular_is_also_plural[@]}"; do
    if [[ $element == "$1" ]]; then
      return 0
    fi
  done
  return 1
}

#
#
#

WORD=$1
shift

# no words in this example code will ever be 2 letters or less.
if [[ ${#WORD} -lt 2 ]]; then
  echo $WORD
  exit 0
fi

#
# an incomplete check, but show-cases the "fun" of the english language.
#

if isAlsoPlural "$WORD"; then
  echo "$WORD"
  exit 0
fi

# fish is actually a tricky one, a school of fish is singular, but a group of difference species of fish is fishes.

LAST="${WORD: -1}"
LAST2="${WORD: -2}"

if [[ $LAST  == "s"  ]] || \
   [[ $LAST  == "x"  ]] || \
   [[ $LAST  == "o"  ]] || \
   [[ $LAST2 == "sh" ]] || \
   [[ $LAST2 == "ch" ]]; then
    echo "${WORD}es"
else
    # Otherwise, just add "s"
    echo "${WORD}s"
fi

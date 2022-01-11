#!/bin/bash

# It must check for master branch and tag only if current branch is master.

YELLOW="\033[1;33m"
GREEN='\033[1;32m'
NOCOLOR="\033[0m"

QUESTION_FLAG="${GREEN}?"

branch=$(git branch | sed -n -e 's/^\* \(.*\)/\1/p')

if [[ "${branch}" = "main" ]]; then

    # Check if your working tree is cleaned or not.
    git diff-index --quiet HEAD
    if [ $? = 1 ] ; then
        echo -e "⚠️ Working tree must be empty before tagging the version."
        exit 1
    fi

    # Check if your current source is not already tagged by using current hash
    GIT_COMMIT=$(git rev-parse HEAD)
    NEEDS_TAG=$(git describe --contains "${GIT_COMMIT}")
    # Only tag if no tag already (would be better if the git describe command above could have a silent option)
    if [ -n "$NEEDS_TAG" ]; then
        echo -e "$⚠️ Latest commit is already tagged. Aborting now..."
        exit 0
    fi

    major=$( (grep "'major'" < build.gradle) | awk '{gsub(/"/, "", $2); print substr($2, 1, length($2)-1)}' )
    minor=$( (grep "'minor'" < build.gradle) | awk '{gsub(/"/, "", $2); print substr($2, 1, length($2)-1)}' )
    patch=$( (grep "'patch'" < build.gradle) | awk '{gsub(/"/, "", $2); print substr($2, 1, length($2)-1)}' )
    build=$( (grep "'build'" < build.gradle) | awk '{gsub(/"/, "", $2); print substr($2, 1, length($2)-1)}' )

    version="${major}.${minor}.${patch}-${build}"
    git tag -a "v${version}" -m "BlissLauncher release version ${version}"
    echo -e "$❓ Do you want to push tag to remote now?[Y/n]: ${NOCOLOR}"
    read -r -p "" response
    response=${response,,}
    if [[ "${response}" =~ ^(yes|y| ) ]] || [ -z "${response}" ]; then
        git push origin --tags
    else
        exit 1
    fi
else
    echo -e "⚠️ Can only be used on main branch.${NOCOLOR}"
fi

New scheduler


1. it relies on the existing query queue mechanism (no changes to that part)
2. changes the role of TwitterQuerySolver and replaces it with new TwitterQuery Manager
3. it obsolets TweetManager and introduce TwitterAPI class
4. Both obsolete classes are still there but has been moved to other package and marked @obsolete. 
5. it is still possible to use old scheduler via capture configuration file capture.properties and field XXXX
6. we can give it some time and test the new scheduler before completely removing old code
7. by default the new one is used since now

how does it work



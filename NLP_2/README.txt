README.txt

Name: Dhruv Purushottam
UNI: dp2631
HW#2: COMS 4705

Java files included:
1. RareCountGenerator.java
2. RarecountHelpers.java
3. GTreeNode.java
4. CKYTagger.java
5. BackPointer.java
6. Grammar.java

Further details about these files are described in code comments.

The instructions below are for running my code to perform vertical markovization. However, results
and files produced without vertical markovization are included in the submission. In some places, 
old code has been retained in comments. I apologize for not having a run.sh file this time. I didn't
get the time to make one but I will for the next assignment.

Instructions for compiling and running: 
1. All source files are in Java. Run "javac *.java" to compile all java files. 
2. Run "java RareCountGenerator" to generate 'parse_train_vert_rare.dat'
3. We can now create a count file with vertical markovization and rare words accounted for: 
run "python count_cfg_freq.py parse_train_vert_rare.dat > cfg_vert_rare.counts" to create
the count file.
4. Now we can run the CKYTagger using the produced counts. Run "java CKYTagger" and see the results 
in prediction_file_vert.dat

Notes about other included files: 
To see results without markovization, see parse_train_rare.dat to see the result of part 4 code
replacing rare words in the corpus with "_RARE_". prediction_file.dat contains the predictions without
markovization.
I've also included the JSON libary needed to parse JSON objects and arrays in java, found in org/json.
All previous count files have also been included.

Results from the CKYTagger WITHOUT the vertical markovization:

      Type       Total   Precision      Recall     F1 Score
===============================================================
         .         370     1.000        1.000        1.000
       ADJ         164     0.827        0.555        0.664
      ADJP          29     0.333        0.241        0.280
  ADJP+ADJ          22     0.542        0.591        0.565
       ADP         204     0.955        0.946        0.951
       ADV          64     0.688        0.516        0.589
      ADVP          30     0.273        0.100        0.146
  ADVP+ADV          53     0.756        0.642        0.694
      CONJ          53     1.000        1.000        1.000
       DET         167     0.988        0.976        0.982
      NOUN         671     0.749        0.842        0.793
        NP         884     0.632        0.529        0.576
    NP+ADJ           2     0.286        1.000        0.444
    NP+DET          21     0.783        0.857        0.818
   NP+NOUN         131     0.641        0.573        0.605
    NP+NUM          13     0.214        0.231        0.222
   NP+PRON          50     0.980        0.980        0.980
     NP+QP          11     0.500        0.091        0.154
       NUM          93     0.983        0.613        0.755
        PP         208     0.593        0.630        0.611
      PRON          14     1.000        0.929        0.963
       PRT          45     0.957        0.978        0.967
   PRT+PRT           2     0.333        1.000        0.500
        QP          26     0.688        0.423        0.524
         S         587     0.624        0.782        0.694
      SBAR          25     0.091        0.040        0.056
      VERB         283     0.683        0.799        0.736
        VP         399     0.553        0.589        0.570
   VP+VERB          15     0.250        0.267        0.258

     total        4664     0.713        0.713        0.713

Results from the CKYTagger WITH the vertical markovization:

      Type       Total   Precision      Recall     F1 Score
===============================================================
         .         370     1.000        1.000        1.000
       ADJ         164     0.689        0.622        0.654
      ADJP          29     0.324        0.414        0.364
  ADJP+ADJ          22     0.591        0.591        0.591
       ADP         204     0.960        0.951        0.956
       ADV          64     0.774        0.641        0.701
      ADVP          30     0.455        0.167        0.244
  ADVP+ADV          53     0.700        0.660        0.680
      CONJ          53     1.000        1.000        1.000
       DET         167     0.988        0.994        0.991
      NOUN         671     0.794        0.845        0.819
        NP         884     0.616        0.548        0.580
    NP+ADJ           2     0.333        0.500        0.400
    NP+DET          21     0.944        0.810        0.872
   NP+NOUN         131     0.606        0.656        0.630
    NP+NUM          13     0.375        0.231        0.286
   NP+PRON          50     0.980        0.980        0.980
     NP+QP          11     0.500        0.091        0.154
       NUM          93     0.910        0.656        0.762
        PP         208     0.621        0.630        0.625
      PRON          14     1.000        0.929        0.963
       PRT          45     1.000        0.933        0.966
   PRT+PRT           2     0.286        1.000        0.444
        QP          26     0.650        0.500        0.565
         S         587     0.703        0.814        0.755
      SBAR          25     0.625        0.400        0.488
      VERB         283     0.788        0.813        0.800
        VP         399     0.662        0.677        0.669
   VP+VERB          15     0.294        0.333        0.312

     total        4664     0.741        0.741        0.741

Vertical markovization improves the performance significantly, especially when we
look at tags like SBAR:

VERB without markovization: 
      SBAR          25     0.091        0.040        0.056

Verb WITH markovization: 
      SBAR          25     0.625        0.400        0.488

However, with some tags like NP+QP, we continue to see a poor performance: 

Without Markovization:
     NP+QP          11     0.500        0.091        0.154

With Markovization: 
     NP+QP          11     0.500        0.091        0.154


The run time looks to be the same (approximately 20 seconds for 250 sentences). Using only
HashMaps and O(1) access tables to perform lookups helped to maintain a high 
speed of parsing. I also only compute pi(i, i, X) values for the possible tags
for the word at index i, so any pi(i, i, X) = 0 is never computed since it isn't 
necessary. I believe this improves the algorithms speed and also allowed me to change
very little for vertical markovization in part 6. 
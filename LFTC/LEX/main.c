/*** Definition Section ***/
%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct nodeTS {
    char *key;
    struct nodeTS *left, *right;
    int position;
};

    struct nodeFIP {
        struct nodeFIP *next;
        char* atomLexical;
        int codAtom;
        int codTS;
    };

    struct nodeFIP* headFIP;
    struct nodeTS* headTS;
    int errorFound = 0;
    int lineNumber = 1;

%}

%option noyywrap
%option yylineno

/*** Rule Section ***/
%%
"=="                                  { insertFIP(yytext, 2); }
"!="                                  { insertFIP(yytext, 3); }
"<="                                  { insertFIP(yytext, 4); }
">="                                  { insertFIP(yytext, 5); }
"++"                                  { insertFIP(yytext, 6); }
"--"                                  { insertFIP(yytext, 7); }
"="                                   { insertFIP(yytext, 8); }
"<"                                   { insertFIP(yytext, 9); }
">"                                   { insertFIP(yytext, 10); }
";"                                   { insertFIP(yytext, 11); }
","                                   { insertFIP(yytext, 12); }
":"                                   { insertFIP(yytext, 13); }
"("                                   { insertFIP(yytext, 14); }
")"                                   { insertFIP(yytext, 15); }
"["                                   { insertFIP(yytext, 16); }
"]"                                   { insertFIP(yytext, 17); }
"{"                                   { insertFIP(yytext, 18); }
"}"                                   { insertFIP(yytext, 19); }
"+"                                   { insertFIP(yytext, 20); }
"-"                                   { insertFIP(yytext, 21); }
"*"                                   { insertFIP(yytext, 22); }
"/"                                   { insertFIP(yytext, 23); }
"%"                                   { insertFIP(yytext, 24); }
"?"                                   { insertFIP(yytext, 25); }
"import"                              { insertFIP(yytext, 26); }
"class"                               { insertFIP(yytext, 27); }
"public"                              { insertFIP(yytext, 28); }
"protected"                           { insertFIP(yytext, 29); }
"private"                             { insertFIP(yytext, 30); }
"static"                              { insertFIP(yytext, 31); }
"abstract"                            { insertFIP(yytext, 32); }
"final"                               { insertFIP(yytext, 33); }
"void"                                { insertFIP(yytext, 34); }
"int"                                 { insertFIP(yytext, 35); }
"double"                              { insertFIP(yytext, 36); }
"String"                              { insertFIP(yytext, 37); }
"new"                                 { insertFIP(yytext, 48); }
"while"                               { insertFIP(yytext, 39); }
"return"                              { insertFIP(yytext, 40); }
"java.util.Scanner"                   { insertFIP(yytext, 41); }
"Scanner"                             { insertFIP(yytext, 42); }
"System.in"                           { insertFIP(yytext, 43); }
".nextInt"                            { insertFIP(yytext, 44); }
".nextDouble"                         { insertFIP(yytext, 45); }
".nextLine"                           { insertFIP(yytext, 46); }
"System.out.println"                  { insertFIP(yytext, 47); }
[a-zA-Z][a-zA-Z0-9_]{1,8}             { insertFIP(yytext, 0); insertTS(yytext); }
([0-9][.]|[1-9]+[.])[0-9]+            { insertFIP(yytext, 1); insertTS(yytext); }
[\n]								  { ++lineNumber; }
[ \t\r]+             				  { ; }
.                					  { errorFound = 1; printf("Illegal token %s at line %d !", yytext, lineNumber); printf("\n"); }
%%

/* yywrap() - wraps the above rule section */
int yywrap() {}

// A utility function to do inorder traversal of BST
void inorder() {
    struct nodeTS *root = headTS;
    if (root != NULL) {
        inorder(root->left);
        printf("%s \n", root->key);
        inorder(root->right);
    }
}

void insertFIP(char* s, int code){
    return;
}

void insertTS(char* s){
    struct nodeTS* crt = headTS;
    struct nodeTS* prev = NULL;
    while (crt != NULL) {
        prev = crt;
        if (strcmp(crt->key, s) == 0){
            return;
        }
        if (strcmp(crt->key, s) < 0){
            crt = crt->left;
        }
        else{
            crt = crt->right;
        }
    }

    struct nodeTS *newNode = (struct nodeTS *) malloc(sizeof(struct nodeTS));
    newNode->key = malloc(strlen(s) * sizeof(char));
    strcpy(newNode->key, s);
    newNode->left = newNode->right = NULL;

    if(prev == NULL) {
        headTS = newNode;
    }
    else if(strcmp(prev->key, s) < 0){
        prev->left = newNode;
    }
    else{
        prev->right = newNode;
    }
}

int main(int argc, char** argv){
    headTS = (struct nodeTS*)malloc(sizeof(struct nodeTS));
    headTS->left = headTS->right = NULL;

    FILE *fp;
    fp = fopen(argv[1], "r");

    /* yyin - takes the file pointer which contains the input*/
    yyin = fp;

    /* yylex() - this is the main flex function which runs the Rule Section*/
    yylex();

    inorder();

    return 0;
}
import spacy
import sys
import os

nlp = spacy.load("it_core_news_sm")

def writeOutput(file, doc):
    l = 0
    for sent in doc.sents:
        file.write("<s>\n")
        i = l
        for token in sent:
            file.write("{0}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t{8}\t{9}\t{10}\t{11}\n".format(
                token.i+1-l,
                token.text,
                token.lemma_,
                token.pos_,
                token.tag_,
                token.dep_,
                0 if token.dep_=='ROOT' else token.head.i+1-l,
                token.ent_iob_ if token.ent_iob_=='O' else token.ent_iob_+"-"+token.ent_type_,
                token.is_punct,
                token.is_space,
                token.is_stop,
                token.shape_
            ))
            i += 1
        file.write('</s>\n')
        l = i

def main():
    nlp = spacy.load("it_core_news_sm")
    with os.scandir(sys.argv[1]) as entries:
        for entry in entries:
            if (entry.is_file() and entry.name.startswith('un_')):
                print('Process file: ' + entry.name + '\n')
                txtfile = open(entry, 'r', -1, 'utf-8')
                outfile = open(sys.argv[2] + '/' + entry.name + '.spacy', 'w', -1, 'utf-8')
                line = txtfile.readline()
                while line:
                    outfile.write("<p>\n")
                    ptext = nlp(line.strip())
                    writeOutput(outfile, ptext)
                    outfile.write("</p>\n")
                    line = txtfile.readline()
                txtfile.close()
                outfile.close()

if __name__ == '__main__':
    main()

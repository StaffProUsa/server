rm -fr bin
path_dir=${PWD##*/}   
rm "${path_dir}.jar"
mkdir bin
cd bin
FILES="../lib/*"
for f in $FILES
do
  echo "Compiling $f"
    jar xf $f
done
rm -fr META-INF/*.SF
rm -fr META-INF/*.RSA
rm -fr META-INF/*.DSA
rm -fr META-INF/MANIFEST.MF
echo "Manifest-Version: 1.0" >> META-INF/MANIFEST.MF
echo "Main-Class: App" >> META-INF/MANIFEST.MF
echo "" >> META-INF/MANIFEST.MF
echo "" >> META-INF/MANIFEST.MF
cd ../
javac -cp "src/:lib/*" src/App.java -d bin
jar cfM "${path_dir}.jar" -C bin/ .
rm -fr bin
#Copy to USUARIO
#scp "${path_dir}.jar" 10:/home/servisofts/servicios/calistenia/entornos/calistenia/servicios/calistenia
#cp "${path_dir}.jar" /Users/rickypazd/Documents/GitHub/kolping/server/lib/
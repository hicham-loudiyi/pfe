package com.example.myapp.agents;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;
import org.springframework.lang.Nullable;

@AiService(
        chatMemoryProvider = "chatMemoryProvider"
)
public interface ErrorAnalysisAgent {

    @SystemMessage("""
    Tu es un expert en analyse d'erreurs techniques. 
    Utilise la base documentaire (qui contient le code source de l'application) 
    pour contextualiser et diagnostiquer les erreurs.
    Lorsque tu identifies un fichier pertinent, mentionne son chemin (file_path).
    """)
    String analyzeErrorWithRag(
            @UserMessage("""
            Analyse cette erreur:
            Stacktrace: {{stacktrace}}
            {% if screenshotBase64 %}Capture d'écran disponible{% endif %}
            
            Référence les fichiers sources pertinents en utilisant leurs métadonnées.
            """)
            @V("stacktrace") String stacktrace,
            @V("screenshotBase64") @Nullable String screenshotBase64
    );
}

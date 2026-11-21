export default {
  async fetch(request, env, ctx) {
    // Handle CORS preflight requests
    if (request.method === "OPTIONS") {
      return new Response(null, {
        headers: {
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
          "Access-Control-Allow-Headers": "Content-Type, Authorization",
        },
      });
    }

    // Health check endpoint (GET request)
    if (request.method === "GET") {
      return new Response(
        JSON.stringify({ status: "Early Enlightenment AI is online" }),
        {
          headers: {
            "Content-Type": "application/json",
            "Access-Control-Allow-Origin": "*",
          },
        }
      );
    }

    // AI Generation endpoint (POST request)
    if (request.method === "POST") {
      try {
        const body = await request.json();
        const question = body.question;

        if (!question || question.trim() === "") {
          return new Response(
            JSON.stringify({ error: "Field 'question' is required." }),
            {
              status: 400,
              headers: {
                "Content-Type": "application/json",
                "Access-Control-Allow-Origin": "*",
              },
            }
          );
        }

        // Run Cloudflare Workers AI binding using Meta Llama 3.1 8B Instruct model
        const aiResponse = await env.AI.run("@cf/meta/llama-3.1-8b-instruct-fast", {
          prompt: question,
        });

        const answerText = aiResponse.response || aiResponse.text || "No response generated.";

        return new Response(
          JSON.stringify({ answer: answerText }),
          {
            status: 200,
            headers: {
              "Content-Type": "application/json",
              "Access-Control-Allow-Origin": "*",
            },
          }
        );
      } catch (err) {
        return new Response(
          JSON.stringify({ error: err.message || "Failed to process AI request." }),
          {
            status: 500,
            headers: {
              "Content-Type": "application/json",
              "Access-Control-Allow-Origin": "*",
            },
          }
        );
      }
    }

    return new Response("Method Not Allowed", { status: 405 });
  },
};

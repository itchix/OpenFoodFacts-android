module Fastlane
  module Actions
    module SharedValues
      GITHUB_API_STATUS_CODE = :GITHUB_API_STATUS_CODE
      GITHUB_API_RESPONSE = :GITHUB_API_RESPONSE
      GITHUB_API_JSON = :GITHUB_API_JSON
    end

    class GithubApiAction < Action
      class << self
        def run(params)
          require 'json'

          http_method = (params[:http_method] || 'GET').to_s.upcase
          url = construct_url(params[:server_url], params[:path], params[:url])
          headers = construct_headers(params[:api_token], params[:api_bearer], params[:headers])
          payload = construct_body(params[:body], params[:raw_body])
          error_handlers = params[:error_handlers] || {}
          secure = params[:secure]

          response = call_endpoint(
            url,
            http_method,
            headers,
            payload,
            secure
          )

          status_code = response[:status]
          result = {
            status: status_code,
            body: response.body || "",
            json: parse_json(response.body) || {}
          }

          if status_code.between?(200, 299)
            UI.verbose("Response:")
            UI.verbose(response.body)
            UI.verbose("---")
            yield(result) if block_given?
          else
            handled_error = error_handlers[status_code] || error_handlers['*']
            if handled_error
              handled_error.call(result)
            else
              UI.error("---")
              UI.error("Request failed:\n#{http_method}: #{url}")
              UI.error("Headers:\n#{headers}")
              UI.error("---")
              UI.error("Response:")
              UI.error(response.body)
              UI.user_error!("GitHub responded with #{status_code}\n---\n#{response.body}")
            end
          end

          Actions.lane_context[SharedValues::GITHUB_API_STATUS_CODE] = result[:status]
          Actions.lane_context[SharedValues::GITHUB_API_RESPONSE] = result[:body]
          Actions.lane_context[SharedValues::GITHUB_API_JSON] = result[:json]

          return result
        end

        #####################################################
        # @!group Documentation
        #####################################################

        def description
          "Call a GitHub API endpoint and get the resulting JSON response"
        end

        def details
          [
            "Calls any GitHub API endpoint. You must provide your GitHub Personal token (get one from [https://github.com/settings/tokens/new](https://github.com/settings/tokens/new)).",
            "Out parameters provide the status code and the full response JSON if valid, otherwise the raw response body.",
            "Documentation: [https://developer.github.com/v3](https://developer.github.com/v3)."
          ].join("\n")
        end

        def available_options
          [
            FastlaneCore::ConfigItem.new(key: :server_url,
                                         env_name: "FL_GITHUB_API_SERVER_URL",
                                         description: "The server url. e.g. 'https://your.internal.github.host/api/v3' (Default: 'https://api.github.com')",
                                         default_value: "https://api.github.com",
                                         optional: true,
                                         verify_block: proc do |value|
                                           UI.user_error!("Please include the protocol in the server url, e.g. https://your.github.server/api/v3") unless value.include?("//")
                                         end),
            FastlaneCore::ConfigItem.new(key: :api_token,
                                         env_name: "FL_GITHUB_API_TOKEN",
                                         description: "Personal API Token for GitHub - generate one at https://github.com/settings/tokens",
                                         conflicting_options: [:api_bearer],
                                         sensitive: true,
                                         code_gen_sensitive: true,
                                         default_value: ENV["GITHUB_API_TOKEN"],
                                         default_value_dynamic: true,
                                         optional: true),
            FastlaneCore::ConfigItem.new(key: :api_bearer,
                                         env_name: "FL_GITHUB_API_BEARER",
                                         sensitive: true,
                                         code_gen_sensitive: true,
                                         description: "Use a Bearer authorization token. Usually generated by Github Apps, e.g. GitHub Actions GITHUB_TOKEN environment variable",
                                         conflicting_options: [:api_token],
                                         optional: true,
                                         default_value: nil),
            FastlaneCore::ConfigItem.new(key: :http_method,
                                         env_name: "FL_GITHUB_API_HTTP_METHOD",
                                         description: "The HTTP method. e.g. GET / POST",
                                         default_value: "GET",
                                         optional: true,
                                         verify_block: proc do |value|
                                           unless %w(GET POST PUT DELETE HEAD CONNECT PATCH).include?(value.to_s.upcase)
                                             UI.user_error!("Unrecognised HTTP method")
                                           end
                                         end),
            FastlaneCore::ConfigItem.new(key: :body,
                                         env_name: "FL_GITHUB_API_REQUEST_BODY",
                                         description: "The request body in JSON or hash format",
                                         skip_type_validation: true, # allow Hash, Array
                                         default_value: {},
                                         optional: true),
            FastlaneCore::ConfigItem.new(key: :raw_body,
                                         env_name: "FL_GITHUB_API_REQUEST_RAW_BODY",
                                         description: "The request body taken verbatim instead of as JSON, useful for file uploads",
                                         optional: true),
            FastlaneCore::ConfigItem.new(key: :path,
                                         env_name: "FL_GITHUB_API_PATH",
                                         description: "The endpoint path. e.g. '/repos/:owner/:repo/readme'",
                                         optional: true),
            FastlaneCore::ConfigItem.new(key: :url,
                                         env_name: "FL_GITHUB_API_URL",
                                         description: "The complete full url - used instead of path. e.g. 'https://uploads.github.com/repos/fastlane...'",
                                         optional: true,
                                         verify_block: proc do |value|
                                           UI.user_error!("Please include the protocol in the url, e.g. https://uploads.github.com") unless value.include?("//")
                                         end),
            FastlaneCore::ConfigItem.new(key: :error_handlers,
                                         description: "Optional error handling hash based on status code, or pass '*' to handle all errors",
                                         type: Hash,
                                         default_value: {},
                                         optional: true),
            FastlaneCore::ConfigItem.new(key: :headers,
                                         env_name: "FL_GITHUB_API_HEADERS",
                                         description: "Optional headers to apply",
                                         type: Hash,
                                         default_value: {},
                                         optional: true),
            FastlaneCore::ConfigItem.new(key: :secure,
                                         env_name: "FL_GITHUB_API_SECURE",
                                         description: "Optionally disable secure requests (ssl_verify_peer)",
                                         type: Boolean,
                                         default_value: true,
                                         optional: true)
          ]
        end

        def output
          [
            ['GITHUB_API_STATUS_CODE', 'The status code returned from the request'],
            ['GITHUB_API_RESPONSE', 'The full response body'],
            ['GITHUB_API_JSON', 'The parsed json returned from GitHub']
          ]
        end

        def return_value
          "A hash including the HTTP status code (:status), the response body (:body), and if valid JSON has been returned the parsed JSON (:json)."
        end

        def authors
          ["tommeier"]
        end

        def example_code
          [
            'result = github_api(
            server_url: "https://api.github.com",
            api_token: ENV["GITHUB_TOKEN"],
            http_method: "GET",
            path: "/repos/:owner/:repo/readme",
            body: { ref: "master" }
          )',
            '# Alternatively call directly with optional error handling or block usage
            GithubApiAction.run(
              server_url: "https://api.github.com",
              api_token: ENV["GITHUB_TOKEN"],
              http_method: "GET",
              path: "/repos/:owner/:repo/readme",
              error_handlers: {
                404 => proc do |result|
                  UI.message("Something went wrong - I couldn\'t find it...")
                end,
                \'*\' => proc do |result|
                  UI.message("Handle all error codes other than 404")
                end
              }
            ) do |result|
              UI.message("JSON returned: #{result[:json]}")
            end
          '
          ]
        end

        def is_supported?(platform)
          true
        end

        def category
          :source_control
        end

        private

        def construct_headers(api_token, api_bearer, overrides)
          require 'base64'
          headers = { 'User-Agent' => 'fastlane-github_api' }
          headers['Authorization'] = "Basic #{Base64.strict_encode64(api_token)}" if api_token
          headers['Authorization'] = "Bearer #{api_bearer}" if api_bearer
          headers.merge(overrides || {})
        end

        def construct_url(server_url, path, url)
          return_url = (server_url && path) ? File.join(server_url, path) : url

          UI.user_error!("Please provide either `server_url` (e.g. https://api.github.com) and 'path' or full 'url' for GitHub API endpoint") unless return_url

          return_url
        end

        def construct_body(body, raw_body)
          body ||= {}

          if raw_body
            raw_body
          elsif body.kind_of?(Hash)
            body.to_json
          elsif body.kind_of?(Array)
            body.to_json
          else
            UI.user_error!("Please provide valid JSON, or a hash as request body") unless parse_json(body)
            body
          end
        end

        def parse_json(value)
          JSON.parse(value)
        rescue JSON::ParserError
          nil
        end

        def call_endpoint(url, http_method, headers, body, secure)
          require 'excon'

          Excon.defaults[:ssl_verify_peer] = secure
          middlewares = Excon.defaults[:middlewares] + [Excon::Middleware::RedirectFollower] # allow redirect in case of repo renames

          UI.verbose("#{http_method} : #{url}")

          connection = Excon.new(url)
          connection.request(
            method: http_method,
            headers: headers,
            middlewares: middlewares,
            body: body,
            debug_request: FastlaneCore::Globals.verbose?,
            debug_response: FastlaneCore::Globals.verbose?
          )
        end
      end
    end
  end
end

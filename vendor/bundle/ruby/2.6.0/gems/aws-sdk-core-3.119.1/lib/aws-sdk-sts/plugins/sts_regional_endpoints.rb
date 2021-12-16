# frozen_string_literal: true

module Aws
  module STS
    module Plugins

      class STSRegionalEndpoints < Seahorse::Client::Plugin

        option(:sts_regional_endpoints,
          default: 'regional',
          doc_type: String,
          docstring: <<-DOCS) do |cfg|
Passing in 'regional' to enable regional endpoint for STS for all supported
regions (except 'aws-global'). Using 'legacy' mode will force all legacy 
regions to resolve to the STS global endpoint.
          DOCS
          resolve_sts_regional_endpoints(cfg)
        end

        private
        
        def self.resolve_sts_regional_endpoints(cfg)
          env_mode = ENV['AWS_STS_REGIONAL_ENDPOINTS']
          env_mode = nil if env_mode == ''
          cfg_mode = Aws.shared_config.sts_regional_endpoints(
            profile: cfg.profile)
          env_mode || cfg_mode || 'regional'
        end

      end

    end
  end
end

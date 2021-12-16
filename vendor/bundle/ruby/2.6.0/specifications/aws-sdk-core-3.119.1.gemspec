# -*- encoding: utf-8 -*-
# stub: aws-sdk-core 3.119.1 ruby lib

Gem::Specification.new do |s|
  s.name = "aws-sdk-core".freeze
  s.version = "3.119.1"

  s.required_rubygems_version = Gem::Requirement.new(">= 0".freeze) if s.respond_to? :required_rubygems_version=
  s.metadata = { "changelog_uri" => "https://github.com/aws/aws-sdk-ruby/tree/version-3/gems/aws-sdk-core/CHANGELOG.md", "source_code_uri" => "https://github.com/aws/aws-sdk-ruby/tree/version-3/gems/aws-sdk-core" } if s.respond_to? :metadata=
  s.require_paths = ["lib".freeze]
  s.authors = ["Amazon Web Services".freeze]
  s.date = "2021-08-20"
  s.description = "Provides API clients for AWS. This gem is part of the official AWS SDK for Ruby.".freeze
  s.homepage = "https://github.com/aws/aws-sdk-ruby".freeze
  s.licenses = ["Apache-2.0".freeze]
  s.rubygems_version = "3.0.3".freeze
  s.summary = "AWS SDK for Ruby - Core".freeze

  s.installed_by_version = "3.0.3" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<jmespath>.freeze, ["~> 1.0"])
      s.add_runtime_dependency(%q<aws-partitions>.freeze, ["~> 1", ">= 1.239.0"])
      s.add_runtime_dependency(%q<aws-sigv4>.freeze, ["~> 1.1"])
      s.add_runtime_dependency(%q<aws-eventstream>.freeze, ["~> 1", ">= 1.0.2"])
    else
      s.add_dependency(%q<jmespath>.freeze, ["~> 1.0"])
      s.add_dependency(%q<aws-partitions>.freeze, ["~> 1", ">= 1.239.0"])
      s.add_dependency(%q<aws-sigv4>.freeze, ["~> 1.1"])
      s.add_dependency(%q<aws-eventstream>.freeze, ["~> 1", ">= 1.0.2"])
    end
  else
    s.add_dependency(%q<jmespath>.freeze, ["~> 1.0"])
    s.add_dependency(%q<aws-partitions>.freeze, ["~> 1", ">= 1.239.0"])
    s.add_dependency(%q<aws-sigv4>.freeze, ["~> 1.1"])
    s.add_dependency(%q<aws-eventstream>.freeze, ["~> 1", ">= 1.0.2"])
  end
end

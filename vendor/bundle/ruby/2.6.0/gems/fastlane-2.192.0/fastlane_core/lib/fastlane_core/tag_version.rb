require "rubygems/version"

module FastlaneCore
  # Utility class to construct a Gem::Version from a tag.
  # Accepts vX.Y.Z and X.Y.Z.
  class TagVersion < Gem::Version
    class << self
      def correct?(tag)
        result = superclass.correct?(version_number_from_tag(tag))

        # It seems like depending on the Ruby env, the result is
        # slightly different. We actually just want `true` and `false`
        # values here
        return false if result.nil?
        return true if result == 0
        return result
      end

      # Gem::Version.new barfs on things like "v0.1.0", which is the style
      # generated by the rake release task. Just strip off any initial v
      # to generate a Gem::Version from a tag.
      def version_number_from_tag(tag)
        tag.sub(/^v/, "")
      end
    end

    def initialize(tag)
      super(self.class.version_number_from_tag(tag))
    end
  end
end
